package com.pptom.robot.runner;

import com.pptom.robot.core.WeChatManager;
import com.pptom.robot.custom.TulingHandler;
import com.pptom.robot.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author tom.tang
 * @date 2018/7/19
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/19
 */
@Component
public class WeChatRunner implements CommandLineRunner {
    @Autowired
    private LoginService loginService;


    @Override
    public void run(String... args) throws Exception {
        boolean login = loginService.login();
        WeChatManager weChatManager = WeChatManager.getInstance();
        if (login) {
            loginService.initWeChatManager();
            loginService.wxStatusNotify();
            loginService.startReceiving();
            //图灵机器人
            TulingHandler weChatMessageHandler = new TulingHandler();
            weChatManager.initMessageHandleExecutor(weChatMessageHandler);
        }
    }
}
