package com.pptom.robot.service.impl;

import com.pptom.robot.service.LoginService;
import com.pptom.robot.util.UrlConstant;
import com.pptom.robot.util.enums.LoginParamEnum;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
@Service
public class LoginServiceImpl implements LoginService {



    @Override
    public boolean login() {
        boolean isLogin = false;
        // 组装登录参数和URL
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(LoginParamEnum.LOGIN_ICON.getName(), LoginParamEnum.LOGIN_ICON.getValue()));
        params.add(new BasicNameValuePair(LoginParamEnum.UUID.getName(), ""));
        params.add(new BasicNameValuePair(LoginParamEnum.TIP.getName(), LoginParamEnum.TIP.getValue()));
        //
        while (!isLogin) {
            // SleepUtils.sleep(time += 1000);
//            long millis = System.currentTimeMillis();
//            params.add(new BasicNameValuePair(LoginParamEnum.R.getName(), String.valueOf(millis / 1579L)));
//            params.add(new BasicNameValuePair(LoginParamEnum._.getName(), String.valueOf(millis)));
//            HttpEntity entity = httpClient.doGet(UrlConstant.LOGIN_URL, params, true, null);
//
//            try {
//                String result = EntityUtils.toString(entity);
//                String status = checklogin(result);
//                if (ResultEnum.SUCCESS.getCode().equals(status)) {
//                    processLoginInfo(result); // 处理结果
//                    isLogin = true;
//                    core.setAlive(isLogin);
//                    break;
//                }
//                if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
//                    LOG.info("请点击微信确认按钮，进行登陆");
//                }
//
//            } catch (Exception e) {
//                LOG.error("微信登陆异常！", e);
//            }
        }

        return false;
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public boolean getQR(String qrPath) {
        return false;
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
}
