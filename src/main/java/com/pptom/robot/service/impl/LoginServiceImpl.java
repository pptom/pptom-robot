package com.pptom.robot.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pptom.robot.core.MessageProcessor;
import com.pptom.robot.core.WeChatManager;
import com.pptom.robot.domain.WeChatMessage;
import com.pptom.robot.service.LoginService;
import com.pptom.robot.util.HttpClientUtil;
import com.pptom.robot.util.UrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final String passTicket = "pass_ticket";

    private WeChatManager weChatManager;

    private HttpClientUtil httpClientUtil;

    @Value("${com.pptom.data.path}")
    private String dataPath;

    public LoginServiceImpl() {
        weChatManager = WeChatManager.getInstance();
        httpClientUtil = weChatManager.getHttpClientUtil();
    }

    @Override
    public boolean login() {
        //1.获取uuid,通过uuid下载二维码
        boolean isSuccess = getUuid();
        //2.下载二维码到本地
        boolean isDownload = downloadQrCode();
        //3.循环请求，判断是否已扫码
        boolean isLogin = false;
        //登录参数
        Map<String, String> params = new HashMap<>();
        params.put("loginicon", "true");
        params.put("uuid", weChatManager.getUuid());
        params.put("tip", "0");
        //直到登录为止
        while (!isLogin) {
            long millis = System.currentTimeMillis();
            params.put("r", String.valueOf(millis / 1579L));
            params.put("_", String.valueOf(millis));
            String result = httpClientUtil.doGet(UrlConstant.LOGIN_URL, params, null, true);
            Map<String, String> resultMap = processResult(result);
            String code = resultMap.get("window.code");
            if ("200".equals(code)) {
                String redirectUrl = resultMap.get("window.redirect_uri");
                //https://wx2.qq.com/cgi-bin/mmwebwx-bin 或https://wx.qq.com/cgi-bin/mmwebwx-bin
                String url = redirectUrl.substring(0, redirectUrl.lastIndexOf('/'));
                //放到weChatManager中
                weChatManager.putLoginInfo("url", url);
                Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
                boolean isFind = false;
                for (Map.Entry<String, List<String>> entry : possibleUrlMap.entrySet()) {
                    //主页
                    String indexUrl = entry.getKey();
                    if (url.contains(indexUrl)) {
                        //设置
                        String fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
                        String syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
                        weChatManager.putLoginInfo("fileUrl", fileUrl);
                        weChatManager.putLoginInfo("syncUrl", syncUrl);
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    weChatManager.putLoginInfo("fileUrl", url);
                    weChatManager.putLoginInfo("syncUrl", url);
                }
                String deviceid = "e" + String.valueOf(new Random().nextLong()).substring(1, 16);
                weChatManager.putLoginInfo("deviceid", deviceid);
                List<String> list = new ArrayList<>();
                weChatManager.putLoginInfo("BaseRequest", list);
                //完成设置信息后，请求登录
                String text = httpClientUtil.doGet(redirectUrl, null, null, false);
                Document doc = xmlParser(text);
                if (doc != null) {
                    String skey = "skey";
                    String wxsid = "wxsid";
                    String wxuin = "wxuin";
                    weChatManager.putLoginInfo(skey, doc.getElementsByTagName(skey).item(0).getFirstChild().getNodeValue());
                    weChatManager.putLoginInfo(wxsid, doc.getElementsByTagName(wxsid).item(0).getFirstChild().getNodeValue());
                    weChatManager.putLoginInfo(wxuin, doc.getElementsByTagName(wxuin).item(0).getFirstChild().getNodeValue());
                    weChatManager.putLoginInfo(passTicket, doc.getElementsByTagName(passTicket).item(0).getFirstChild().getNodeValue());
                }
                isLogin = true;
                //标记登录状态
                weChatManager.setAlive(true);
                log.info("登录成功!");
            } else if ("201".equals(code)) {
                log.info("请在手机上确认登录!");
            } else {
                log.info("请扫描二维码!");
            }
        }
        return isLogin;
    }

    private Document xmlParser(String text) {
        Document doc = null;
        StringReader sr = new StringReader(text);
        InputSource is = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
        } catch (Exception e) {
            log.error("can not parse msg:{}", e.getMessage());
        }
        return doc;
    }

    /**
     * 将返回值转化为map
     * @param result
     * @return
     */
    private Map<String, String> processResult(String result) {
        Map<String, String> resultMap = new HashMap<>();
        String[] split = result.replace("jpg;", "jpg#").split(";");
        if (split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                String[] str = split[i].split("=");
                String key = str[0].trim();
                String value = str[1];
                if (str.length > 2) {
                    for (int j = 2; j < str.length; j++) {
                        value = value + "=" + str[j];
                    }
                }
                if (value.startsWith("{") && value.endsWith("}")) {
                    resultMap.put(key, value.trim());
                } else {
                    resultMap.put(key, value.trim().replace("\"", "").replace("'", ""));
                }
            }
        }
        return resultMap;
    }

    /**
     * 获取uuid
     * @return
     */
    private boolean getUuid() {
        Map<String, String> params = new HashMap<>();
        params.put("appid", "wx782c26e4c19acffb");
        params.put("fun", "new");
        params.put("lang", "zh_CN");
        long now = System.currentTimeMillis();
        params.put("_", String.valueOf(now));
        String result = httpClientUtil.doGet(UrlConstant.UUID_URL, params, null, true);
        String uuid = result.substring(result.indexOf("\"") + 1, result.lastIndexOf("\""));
        weChatManager.setUuid(uuid);
        return true;
    }

    public boolean downloadQrCode() {
        String uuid = weChatManager.getUuid();
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }
        //二维码的图片地址
        String qrUrl = UrlConstant.QRCODE_URL + uuid;
        String qrPath = dataPath + File.separator + "qr.jpg";
        HttpEntity entity = httpClientUtil.doGetForEntity(qrUrl, null,  null, true);
        try {
            OutputStream out = new FileOutputStream(qrPath);
            byte[] bytes = EntityUtils.toByteArray(entity);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
        printQR(qrPath);
        return true;
    }

    private void printQR(String qrPath) {
        //指定的二维码文件路径
        File  file= new File(qrPath);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        for (int i = 0; i < height; i+=10) {
            for (int j = 0; j < width; j+=10) {
                int rgb = bufferedImage.getRGB(i, j);
                if (rgb == -1) {
                    System.out.print("\033[47m  \033[0m");
                } else {
                    System.out.print("\033[40m  \033[0m");
                }
            }
            System.out.println();
        }
    }

    @Override
    public boolean initWeChatManager() {
        weChatManager.setAlive(true);
        //设置最后活跃时间
        weChatManager.setLastNormalRetcodeTime(System.currentTimeMillis());
        // 组装请求URL和参数
        String url = String.format(UrlConstant.INIT_URL, weChatManager.getFromLoginInfo("url")
                , String.valueOf(System.currentTimeMillis() / 3158L)
                , weChatManager.getFromLoginInfo(passTicket));
        //获取基本参数
        Map<String, Object> paramMap = weChatManager.getParamMap();
        //jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writeValueAsString(paramMap);
        } catch (IOException e) {
            log.warn("can not write json to request body!{}", e.getMessage());
        }
        String responseBody = httpClientUtil.doPost(url, requestBody);
        try {
            //转成json
            JsonNode root = objectMapper.readTree(responseBody);
            //获取用户
            JsonNode user = root.get("User");
            //获取同步key
            JsonNode syncKey = root.get("SyncKey");
            Integer inviteStartCount =  root.get("InviteStartCount").asInt();
            weChatManager.putLoginInfo("InviteStartCount", inviteStartCount);
            weChatManager.putLoginInfo("SyncKey", syncKey);
            //获取list并遍历
            StringBuilder sb = new StringBuilder();
            JsonNode list = syncKey.get("List");
            if (list.isArray()) {
                for (JsonNode node : list) {
                    sb.append(node.get("Key").asText()).append("_").append(node.get("Val").asText()).append("|");
                }
            }
            // 1_661706053|2_661706420|3_661706415|1000_1494151022
            String synckey = sb.toString().substring(0, sb.toString().length() - 1);
            weChatManager.putLoginInfo("synckey", synckey);
            weChatManager.setUserName(user.get("UserName").asText());
            weChatManager.setNickName(user.get("NickName").asText());
            weChatManager.setUserSelf(user);
            // chatset
            String chatSet = root.get("ChatSet").asText();
            String[] chatSetArray = chatSet.split(",");
            for (String cs : chatSetArray) {
                if (!cs.contains("@@")) {
                    weChatManager.addGroupIdList(cs);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void wxStatusNotify() {
        // 组装请求URL和参数
        String url = String.format(UrlConstant.STATUS_NOTIFY_URL, weChatManager.getFromLoginInfo(passTicket));
        Map<String, Object> paramMap = weChatManager.getParamMap();
        paramMap.put("Code", "3");
        paramMap.put("FromUserName", "");
        paramMap.put("ToUserName", "");
        paramMap.put("ClientMsgId", System.currentTimeMillis());
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writeValueAsString(paramMap);
        } catch (IOException e) {
            log.warn("can not write json to request body!{}", e.getMessage());
        }
        String responseBody = httpClientUtil.doPost(url, requestBody);
        log.info(String.format("欢迎回来， %s", weChatManager.getNickName()));
    }


    @Override
    public void startReceiving() {
        weChatManager.setAlive(true);
        Runnable runnable = () ->{
            log.info("Started Receiving Thread {}", Thread.currentThread().getName());
            while (weChatManager.isAlive()) {
                Map<String, String> resultMap = syncCheck();
                String retcode = resultMap.get("retcode");
                String selector = resultMap.get("selector");
                if ("0".equals(retcode)) {
                    // 最后收到正常报文时间
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                    weChatManager.setLastNormalRetcodeTime(System.currentTimeMillis());
                    JsonNode reponseJson = syncWeChatMessage();
                    if ("2".equals(selector)) {
                        if (reponseJson != null) {
                            JsonNode messageList = reponseJson.get("AddMsgList");
                            //处理后重新赋值
                            messageList = MessageProcessor.produceMsg(messageList);
                            if (messageList.isArray()) {
                                for (JsonNode msg : messageList) {
                                    try {
                                        String s = objectMapper.writeValueAsString(msg);
                                        WeChatMessage weChatMessage = objectMapper.readValue(s, WeChatMessage.class);
                                        log.info("收到消息一条，来自:[{}], FromUserName:{}",
                                                weChatMessage.getRecommendInfo().getNickName(), weChatMessage.getFromUserName());
                                        weChatManager.addMessageToQueue(weChatMessage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                    } else if (selector.equals("7")) {
                        syncWeChatMessage();
                    } else if (selector.equals("4")) {
                        continue;
                    } else if (selector.equals("3")) {
                        continue;
                    } else if (selector.equals("6")) {
                        if (reponseJson != null) {
                            JsonNode messageList = reponseJson.get("AddMsgList");
                            messageList = MessageProcessor.produceMsg(messageList);
                            JsonNode modContactList = reponseJson.get("ModContactList");
                            if (modContactList.isArray()) {
                                for (JsonNode mod : modContactList) {
                                    // 存在主动加好友之后的同步联系人到本地
                                    weChatManager.getContactList().add(mod);
                                }
                            }
                        }
                    } else {
                        syncWeChatMessage();
                    }

                } else {
                    log.warn("未知错误,retcode:{}", retcode);
                    //todo 重试
                    sleep(2000);
                }
                sleep(100);
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void webWxGetContact() {

    }

    @Override
    public void WebWxBatchGetContact() {

    }


    private Map<String, String> syncCheck() {
        Map<String, String> resultMap = new HashMap<>();
        String url = weChatManager.getFromLoginInfo("syncUrl") + UrlConstant.SYNC_CHECK_URL;
        Map<String, String> params = new HashMap<>();
        params.put("uin", ((String) weChatManager.getFromLoginInfo("wxuin")));
        params.put("sid", ((String) weChatManager.getFromLoginInfo("wxsid")));
        params.put("skey", ((String) weChatManager.getFromLoginInfo("skey")));
        params.put("deviceid", ((String) weChatManager.getFromLoginInfo("pass_ticket")));
        params.put("r", String.valueOf(System.currentTimeMillis()));
        params.put("synckey", (String) weChatManager.getFromLoginInfo("synckey"));
        params.put("_", String.valueOf(System.currentTimeMillis()));
        sleep(100);
        HttpEntity entity = httpClientUtil.doGetForEntity(url, params, null, true);
        if (entity == null) {
            resultMap.put("retcode", "9999");
            resultMap.put("selector", "9999");
            return resultMap;
        }
        try {
            String text = EntityUtils.toString(entity);
            Map<String, String> map = processResult(text);
            String s = map.get("window.synccheck");
            if (!StringUtils.isEmpty(s)) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                JsonNode jsonNode = objectMapper.readTree(s);
                resultMap.put("retcode", jsonNode.get("retcode").asText());
                resultMap.put("selector", jsonNode.get("selector").asText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    /**
     * 同步消息 sync the WeChat messages
     * @return
     */
    private JsonNode syncWeChatMessage() {
        JsonNode jsonNode = null;
        String preUrl = (String) weChatManager.getFromLoginInfo("url");
        String wxsid = (String) weChatManager.getFromLoginInfo("wxsid");
        String skey = (String) weChatManager.getFromLoginInfo("skey");
        String pass_ticket = (String) weChatManager.getFromLoginInfo("pass_ticket");
        String url = String.format(UrlConstant.WEB_WX_SYNC_URL, preUrl, wxsid, skey, pass_ticket);
        //参数
        Map<String, Object> paramMap = weChatManager.getParamMap();
        paramMap.put("SyncKey", weChatManager.getFromLoginInfo("SyncKey"));
        long rr = -System.currentTimeMillis() / 1000;
        paramMap.put("rr", rr);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writeValueAsString(paramMap);
        } catch (IOException e) {
            log.warn("can not write json to request body!{}", e.getMessage());
        }
        String responseBody = httpClientUtil.doPost(url, requestBody);
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            int ret = root.get("BaseResponse").get("Ret").asInt();
            if (ret == 0) {
                jsonNode = root;
                JsonNode syncCheckKey = jsonNode.get("SyncCheckKey");
                weChatManager.putLoginInfo("SyncKey", syncCheckKey);
                JsonNode list = jsonNode.get("SyncKey").get("List");
                StringBuilder sb = new StringBuilder();
                if (list.isArray()) {
                    for (JsonNode node : list) {
                        sb.append(node.get("Key").asText()).append("_").append(node.get("Val").asText()).append("|");
                    }
                }
                String synckey = sb.toString();
                weChatManager.putLoginInfo("synckey", synckey.substring(0, synckey.length() - 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonNode;
    }

    /**
     * 毫秒为单位
     *
     * @param time
     */
    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Map<String, List<String>> getPossibleUrlMap() {
        Map<String, List<String>> possibleUrlMap = new HashMap<>();
        //1
        List<String> wx = new ArrayList<>();
        wx.add("file.wx.qq.com");
        wx.add("webpush.wx.qq.com");
        possibleUrlMap.put("wx.qq.com", wx);
        //2
        List<String> wx2 = new ArrayList<>();
        wx2.add("file.wx2.qq.com");
        wx2.add("webpush.wx2.qq.com");
        possibleUrlMap.put("wx2.qq.com", wx2);
        //3
        List<String> wx8 = new ArrayList<>();
        wx8.add("file.wx8.qq.com");
        wx8.add("webpush.wx8.qq.com");
        possibleUrlMap.put("wx8.qq.com", wx8);
        //4
        List<String> web2 = new ArrayList<>();
        web2.add("file.web2.wechat.com");
        web2.add("webpush.web2.wechat.com");
        possibleUrlMap.put("web2.wechat.com", web2);
        //5
        List<String> web = new ArrayList<>();
        web.add("file.web.wechat.com");
        web.add("webpush.web.wechat.com");
        possibleUrlMap.put("wechat.com", web);
        return possibleUrlMap;
    }
}
