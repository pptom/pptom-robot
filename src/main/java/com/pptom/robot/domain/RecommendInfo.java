package com.pptom.robot.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author tom.tang
 * @date 2018/7/19
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/19
 */
@Getter
@Setter
public class RecommendInfo implements Serializable {
    private static final long serialVersionUID = -4350394907591677148L;
    private String ticket;
    private String userName;
    private int sex;
    private int attrStatus;
    private String city;
    private String nickName;
    private int scene;
    private String province;
    private String content;
    private String alias;
    private String signature;
    private int opCode;
    private int qQNum;
    private int verifyFlag;
}
