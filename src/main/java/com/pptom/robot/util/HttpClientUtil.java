package com.pptom.robot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tom.tang
 * @date 2018/7/18
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/18
 */
@Slf4j
public class HttpClientUtil {

    private static CloseableHttpClient httpClient;

    private static HttpClientUtil instance = null;

    private static CookieStore cookieStore;

    static {
        cookieStore = new BasicCookieStore();
        // 将CookieStore设置到httpClient中
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    private HttpClientUtil() {
    }

    public static HttpClientUtil getInstance() {
        if (instance == null) {
            synchronized (HttpClientUtil.class) {
                if (instance == null) {
                    instance = new HttpClientUtil();
                }
            }
        }
        return instance;
    }

    public static String getCookie(String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 发起get请求
     * @param url
     * @param params
     * @param header
     * @param isRedirect
     * @return
     */
    public String doGet(String url, Map<String, String> params, Map<String, String> header, boolean isRedirect) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            HttpEntity httpEntity = doGetForEntity(url, params, header, isRedirect);
            String result = null;
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity, "utf-8");
            }
            /*
             * 释放资源
             */
            EntityUtils.consume(httpEntity);
            return result;
        } catch (Exception e) {
            log.error("url : {} http client request exception:{}", url, e);
        }
        return null;
    }

    /**
     * 发起get请求
     * @param url
     * @param params
     * @param header
     * @param isRedirect
     * @return
     */
    public HttpEntity doGetForEntity(String url, Map<String, String> params, Map<String, String> header, boolean isRedirect) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<BasicNameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, "UTF-8"));
            }
            final HttpGet httpGet = new HttpGet(url);
            //设置请求头
            if (header != null && !header.isEmpty()) {
                header.forEach(httpGet::setHeader);
            }
            if (!isRedirect) {
                // 禁止重定向
                httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
            }
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 返回码校验
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                log.warn("status code : {}", url, statusCode);
            }
            //读取响应结果
            HttpEntity entity = response.getEntity();
            return entity;
        } catch (Exception e) {
            log.error("url : {} http client request exception:{}", url, e);
        }
        return null;
    }

    /**
     * 发送post请求
     * @param url
     * @param requestBody
     * @return
     */
    public String doPost(String url, String requestBody) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(requestBody, "UTF-8");
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                log.error("http请求{}失败, error status code : {}", url, statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            log.error("url : {} http client request exception:{}", url, e);
        }
        return null;
    }

    public static void main(String[] args) {
//        testDoGet();
        String result = "window.QRLogin.code = 200; window.QRLogin.uuid = \"AYTiXKTz0g==\";";
        String uuid = result.substring(result.indexOf("\"") + 1, result.lastIndexOf("\""));
        System.out.println(uuid);
    }

    private static void testDoGet() {
        HttpClientUtil httpClientUtil = HttpClientUtil.getInstance();
        Map<String, String> params = new HashMap<>();
        params.put("appid", "wx782c26e4c19acffb");
        params.put("fun", "new");
        params.put("lang", "zh_CN");
        long now = System.currentTimeMillis();
        params.put("_", String.valueOf(now));
        String result = httpClientUtil.doGet(UrlConstant.UUID_URL, params, null, true);
        log.info("result:[{}]", result);
    }
}
