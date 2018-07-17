package com.pptom.robot.service;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
public interface LoginService {

    /**
     * 登录
     * @return
     */
    boolean login();

    /**
     * 获取UUID
     * @return
     */
    String getUuid();

    /**
     * 获取二维码图片
     * @param qrPath
     * @return
     */
    boolean getQR(String qrPath);

    /**
     * web初始化
     * @return
     */
    boolean webWxInit();

    /**
     * 微信状态通知
     */
    void wxStatusNotify();

    /**
     * 接收消息
     */
    void startReceiving();

    /**
     * 获取微信联系人
     */
    void webWxGetContact();

    /**
     * 批量获取联系人信息
     */
    void WebWxBatchGetContact();
}
