package com.pptom.robot.service.impl;

import com.pptom.robot.core.WeChatManager;
import com.pptom.robot.service.LoginService;
import com.pptom.robot.util.HttpClientUtil;
import com.pptom.robot.util.UrlConstant;
import com.pptom.robot.util.enums.LoginParamEnum;
import com.pptom.robot.util.enums.LoginResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                String uri = resultMap.get("window.redirect_uri");
                //https://wx2.qq.com/cgi-bin/mmwebwx-bin 或https://wx.qq.com/cgi-bin/mmwebwx-bin
                String url = uri.substring(0, uri.lastIndexOf('/'));
                //放到weChatManager中
                weChatManager.getLoginInfo().put("url", url);
                Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
                boolean isFind = false;
                for (Map.Entry<String, List<String>> entry : possibleUrlMap.entrySet()) {
                    //主页
                    String indexUrl = entry.getKey();
                    if (url.contains(indexUrl)) {
                        //设置
                        String fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
                        String syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
                        weChatManager.getLoginInfo().put("fileUrl", fileUrl);
                        weChatManager.getLoginInfo().put("syncUrl", syncUrl);
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    weChatManager.getLoginInfo().put("fileUrl", url);
                    weChatManager.getLoginInfo().put("syncUrl", url);
                }


                isLogin = true;
                log.info("登录成功!");
            } else if ("201".equals(code)) {
                log.info("请在手机上确认登录!");
            } else {
                log.info("请扫描二维码!");
            }
        }
        return isLogin;
    }

    private Map<String, String> processResult(String result) {
        Map<String, String> resultMap = new HashMap<>();
        String[] split = result.split(";");
        if (split != null && split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                String[] str = split[i].split("=");
                String key = str[0].trim();
                String value = str[1].trim().replace("\"", "").replace("'", "");
                resultMap.put(key, value);
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
    public boolean webWxInit() {
        return false;
    }

    @Override
    public void wxStatusNotify() {

    }

    @Override
    public void startReceiving() {

    }

    @Override
    public void webWxGetContact() {

    }

    @Override
    public void WebWxBatchGetContact() {

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
