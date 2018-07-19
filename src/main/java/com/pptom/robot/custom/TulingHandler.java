package com.pptom.robot.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pptom.robot.core.WeChatMessageHandler;
import com.pptom.robot.domain.ResultBean;
import com.pptom.robot.domain.WeChatMessage;
import com.pptom.robot.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author tom.tang
 * @date 2018/7/19
 * @email tom.tang@sainstore.com
 * @description 请求参数格式为 json
 * 请求示例：
 *
 * {
 * 	"reqType":0,
 *     "perception": {
 *         "inputText": {
 *             "text": "附近的酒店"
 *         },
 *         "inputImage": {
 *             "url": "imageUrl"
 *         },
 *         "selfInfo": {
 *             "location": {
 *                 "city": "北京",
 *                 "province": "北京",
 *                 "street": "信息路"
 *             }
 *         }
 *     },
 *     "userInfo": {
 *         "apiKey": "",
 *         "userId": ""
 *     }
 * }
 * @since 2018/7/19
 */
@Slf4j
public class TulingHandler implements WeChatMessageHandler {

    private final static String URL = "http://openapi.tuling123.com/openapi/api/v2";

    private final static String ROBOT_APIKEY = "863551b700bb4982baaecc8c954e7a22";

    private ObjectMapper objectMapper = new ObjectMapper();

    private HttpClientUtil httpClientUtil = HttpClientUtil.getInstance();

    private String getRequestBody(String text){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("reqType", 0);
        objectNode.with("perception").with("inputText").put("text", text);
        String userId = System.currentTimeMillis() + "";
        objectNode.with("userInfo").put("apiKey", ROBOT_APIKEY).put("userId", userId);
        try {
            String result = objectMapper.writeValueAsString(objectNode);
            return result;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String replyContent = "你联系的人正忙，请稍后联系!";
        System.out.println(replyContent);
    }

    @Override
    public ResultBean handleText(WeChatMessage weChatMessage) {
        String text = weChatMessage.getText();
        String replyContent = "你联系的人正忙，请稍后联系!";
//        if (text.startsWith("pp")) {
        if (!StringUtils.isEmpty(text)) {
//            if ("pp".equals(text)) {
//                text = "你是谁";
//            } else if (text.length() > 2) {
//                text = text.substring(2, text.length());
//            }
            String requestBody = getRequestBody(text);
            String response = httpClientUtil.doPost(URL, requestBody);
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode jsonNode = mapper.readTree(response);
                JsonNode results = jsonNode.get("results");
                if (results.isArray()) {
                    for (JsonNode result : results) {
                        String resultType = result.get("resultType").asText();
                        if ("text".equals(resultType)) {
                            replyContent = result.get("values").get("text").asText();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResultBean(true, replyContent);
        }
        return new ResultBean(false, replyContent);
    }

    @Override
    public ResultBean handleImage(WeChatMessage weChatMessage) {
        return new ResultBean();
    }

    @Override
    public ResultBean handleVoice(WeChatMessage weChatMessage) {
        return new ResultBean();
    }

    @Override
    public ResultBean handleVideo(WeChatMessage weChatMessage) {
        return new ResultBean();
    }

    @Override
    public ResultBean handleBusinessCard(WeChatMessage weChatMessage) {
        return new ResultBean();
    }

    @Override
    public ResultBean handleSystem(WeChatMessage weChatMessage) {
        return new ResultBean();
    }

    @Override
    public ResultBean handleAddFriend(WeChatMessage weChatMessage) {
        return new ResultBean();
    }

    @Override
    public ResultBean handleFile(WeChatMessage weChatMessage) {
        return new ResultBean();
    }
}
