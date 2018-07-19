package com.pptom.robot.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */

public class ResultBean {

    private boolean reply = false;

    private String replyContent;

    public ResultBean(boolean reply, String replyContent) {
        this.reply = reply;
        this.replyContent = replyContent;
    }

    public ResultBean() {
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
