package com.pptom.robot;

import com.pptom.robot.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class PptomRobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PptomRobotApplication.class, args);
    }

    @Autowired
    private LoginService loginService;


    @Bean
    public CommandLineRunner startWeChat() {
        return (args) -> {
            loginService.login();
        };
    }

    /**
     * 毫秒为单位
     *
     * @param time
     */
    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
