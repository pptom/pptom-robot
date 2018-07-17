package com.pptom.robot.util;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
public class UrlConstant {
    private UrlConstant(){

    }

    /**
     * 基本的URL
     */
    public static final String BASE_URL = "https://login.weixin.qq.com";

    /**
     * UUID的URL
     */
    public static final String UUID_URL = BASE_URL + "/jslogin";
    /**
     * 初始化URL
     */
    public static final String QRCODE_URL = BASE_URL + "/qrcode/";
    /**
     * 微信状态通知
     */
    public static final String STATUS_NOTIFY_URL = BASE_URL + "/webwxstatusnotify?lang=zh_CN&pass_ticket=%s";
    /**
     * 登陆URL
     */
    public static final String LOGIN_URL = BASE_URL + "/cgi-bin/mmwebwx-bin/login";

    /**
     * 初始化URL
     */
    public static final String INIT_URL = "%s/webwxinit?r=%s&pass_ticket=%s";
    /**
     * 检查心跳URL
     */
    public static final String SYNC_CHECK_URL = "/synccheck";
    /**
     * web微信消息同步URL
     */
    public static final String WEB_WX_SYNC_URL = "%s/webwxsync?sid=%s&skey=%s&pass_ticket=%s";
    /**
     * web微信获取联系人信息URL
     */
    public static final String WEB_WX_GET_CONTACT = "%s/webwxgetcontact";

    /**
     * 发送消息URL
     */
    public static final String WEB_WX_SEND_MSG = "%s/webwxsendmsg";
    /**
     * 上传文件到服务器
     */
    public static final String WEB_WX_UPLOAD_MEDIA = "%s/webwxuploadmedia?f=json";
    /**
     * 下载图片消息
     */
    public static final String WEB_WX_GET_MSG_IMG = "%s/webwxgetmsgimg";

    /**
     * 下载语音消息
     */
    public static final String WEB_WX_GET_VOICE = "%s/webwxgetvoice";

    /**
     * 下载视频消息
     */
    public static final String WEB_WX_GET_VIEDO = "%s/webwxgetvideo";

    /**
     * 不扫码登陆
     */
    public static final String WEB_WX_PUSH_LOGIN = "%s/webwxpushloginurl";

    /**
     * 退出微信
     */
    public static final String WEB_WX_LOGOUT = "%s/webwxlogout";

    /**
     * 查询群信息
     */
    public static final String WEB_WX_BATCH_GET_CONTACT = "%s/webwxbatchgetcontact?type=ex&r=%s&lang=zh_CN&pass_ticket=%s";

    /**
     * 修改好友备注
     */
    public static final String WEB_WX_REMARKNAME = "%s/webwxoplog?lang=zh_CN&pass_ticket=%s";

    /**
     * 被动添加好友
     */
    public static final String WEB_WX_VERIFYUSER = "%s/webwxverifyuser?r=%s&lang=zh_CN&pass_ticket=%s";

    /**
     * 下载文件
     */
    public static final String WEB_WX_GET_MEDIA = "%s/webwxgetmedia";

}
