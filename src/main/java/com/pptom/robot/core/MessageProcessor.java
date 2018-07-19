package com.pptom.robot.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pptom.robot.domain.ResultBean;
import com.pptom.robot.domain.WeChatMessage;
import com.pptom.robot.util.HttpClientUtil;
import com.pptom.robot.util.MessageCodeConstant;
import com.pptom.robot.util.UrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tom.tang
 * @date 2018/7/19
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/19
 */
@Slf4j
public class MessageProcessor {

    private static WeChatManager weChatManager = WeChatManager.getInstance();

    private static HttpClientUtil httpClientUtil = weChatManager.getHttpClientUtil();

    public static JsonNode produceMsg(JsonNode messageList) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        if (messageList.isArray()) {
            for (JsonNode message : messageList) {
//                Map<String, Object> map = new HashMap<>();
                // 是否是群消息
                ObjectNode objectNode = (ObjectNode) message;
                objectNode.put("groupMsg", false);
                String fromUserName = message.get("FromUserName").asText();
                String toUserName = message.get("ToUserName").asText();
                //消息内容
                String content = message.get("Content").asText();
                //判断是否群聊消息
                if (fromUserName.contains("@@") || toUserName.contains("@@")) {
                    if (fromUserName.contains("@@")) {
                        weChatManager.addGroupIdList(fromUserName);
                    } else if (toUserName.contains("@@")) {
                        weChatManager.addGroupIdList(toUserName);
                    }
                    // 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，
                    // 这里需要处理一下，去掉多余信息，只保留消息内容
                    if (content.contains("<br/>")) {
                        content = content.substring(content.indexOf("<br/>") + 5);
                        objectNode.put("Content", content);
                        objectNode.put("groupMsg", true);
                    }
                } else {
                    //todo 处理消息内容
                }
                /**
                 * TEXT("Text", "文本消息"),
                 * 	PIC("Pic", "图片消息"),
                 * 	VOICE("Voice", "语音消息"),
                 * 	VIEDO("Viedo", "小视频消息"),
                 * 	NAMECARD("NameCard", "名片消息"),
                 * 	SYS("Sys", "系统消息"),
                 * 	VERIFYMSG("VerifyMsg", "添加好友"),
                 * 	MEDIA("app", "文件消息");
                 */
                Integer msgType = message.get("MsgType").asInt();
                objectNode.put("type", msgType);
                if (MessageCodeConstant.TEXT.equals(msgType)) {
                    //文本消息
                    String url = message.get("Url").asText();
                    if (!StringUtils.isEmpty(url)) {
                        String regEx = "(.+?\\(.+?\\))";
                        String data = find(content, regEx);
                        objectNode.put("Text", data);
                    } else {
                        objectNode.put("Text", content);
                    }
                }
                arrayNode.add(objectNode);
            }
        }
        return arrayNode;
    }

    /**
     * 回复信息
     * @param resultBean
     * @param weChatMessage
     */
    public static void sendMessage(ResultBean resultBean, WeChatMessage weChatMessage) {
        String replyContent = resultBean.getReplyContent();
        if (resultBean.isReply() && !StringUtils.isEmpty(replyContent)) {
            String fromUserName = weChatMessage.getFromUserName();
            sleep(2000);
            sendMessage(1, replyContent, fromUserName);
        }
    }

    /**
     * 毫秒为单位
     *
     * @param time
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void sendMessage(int msgType, String content, String toUserName) {
        String userName = weChatManager.getUserName();
        if (toUserName != null && !toUserName.equals(userName)) {
            String url = String.format(UrlConstant.WEB_WX_SEND_MSG, weChatManager.getFromLoginInfo("url"));
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("Type", msgType);
            messageMap.put("Content", content);
            messageMap.put("FromUserName", userName);
            messageMap.put("ToUserName", toUserName == null ? userName : toUserName);
            messageMap.put("LocalID", System.currentTimeMillis() * 10);
            messageMap.put("ClientMsgId", System.currentTimeMillis() * 10);
            Map<String, Object> paramMap = weChatManager.getParamMap();
            paramMap.put("Msg", messageMap);
            paramMap.put("Scene", 0);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String requestBody = objectMapper.writeValueAsString(paramMap);
                String result = httpClientUtil.doPost(url, requestBody);
                log.info("发送信息给:[{}], 内容:[{}]", toUserName, content);
                log.info("发送信息结果:[{}]", result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据正则查找
     *
     * @param str
     * @param regex
     * @return
     */
    protected static String find(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        String result = "";
        if (m.find()) {
            result = m.group(1);
        }
        return result.trim();
    }
}
