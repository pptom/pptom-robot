package com.pptom.robot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeChatMessage implements Serializable {
    private static final long serialVersionUID = -155168682084829519L;
    private int subMsgType;
    private int voiceLength;
    private String fileName;
    private int imgHeight;
    private String toUserName;
    private int hasProductId;
    private int imgStatus;
    private String url;
    private int imgWidth;
    private int forwardFlag;
    private int status;
    private String Ticket;
    /** 推荐消息报文 **/
    private RecommendInfo recommendInfo;
    private long createTime;
    private String newMsgId;
    /** 文本消息内容 **/
    private String text;
    /** 消息类型 **/
    private int msgType;
    /** 是否为群消息 **/
    private boolean groupMsg;
    private String msgId;
    private int statusNotifyCode;
//    private AppInfo appInfo;
    private int appMsgType;
    private int type;
    private int playLength;
    private String mediaId;
    private String content;
    private String statusNotifyUserName;
    /** 消息发送者ID **/
    private String fromUserName;
    private String oriContent;
    private String fileSize;
}
