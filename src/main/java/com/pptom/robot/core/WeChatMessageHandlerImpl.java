package com.pptom.robot.core;

import com.pptom.robot.domain.ResultBean;
import com.pptom.robot.domain.WeChatMessage;

/**
 * @author tom.tang
 * @date 2018/7/19
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/19
 */
public class WeChatMessageHandlerImpl implements WeChatMessageHandler {
    @Override
    public ResultBean handleText(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleImage(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleVoice(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleVideo(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleBusinessCard(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleSystem(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleAddFriend(WeChatMessage weChatMessage) {
        return null;
    }

    @Override
    public ResultBean handleFile(WeChatMessage weChatMessage) {
        return null;
    }
}
