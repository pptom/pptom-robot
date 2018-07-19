package com.pptom.robot.core;

import com.pptom.robot.domain.ResultBean;
import com.pptom.robot.domain.WeChatMessage;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
public interface WeChatMessageHandler {
    /**
     * 处理文本信息
     * @param weChatMessage
     * @return
     */
    ResultBean handleText(WeChatMessage weChatMessage);

    /**
     * 处理图片消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleImage(WeChatMessage weChatMessage);

    /**
     * 处理语音消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleVoice(WeChatMessage weChatMessage);

    /**
     * 处理视频消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleVideo(WeChatMessage weChatMessage);

    /**
     * 处理名片消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleBusinessCard(WeChatMessage weChatMessage);

    /**
     * 处理系统消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleSystem(WeChatMessage weChatMessage);

    /**
     * 处理确认添加好友消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleAddFriend(WeChatMessage weChatMessage);

    /**
     * 处理收到文件消息
     * @param weChatMessage
     * @return
     */
    ResultBean handleFile(WeChatMessage weChatMessage);
}
