package com.pptom.robot.util.enums;

/**
 * @author tom.tang
 * @date 2018/7/18
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/18
 */
public enum LoginResultEnum {

    SUCCESS("200", "成功"),
    WAIT_CONFIRM("201", "请在手机上点击确认"),
    WAIT_SCAN("400", "请扫描二维码");

    private String code;
    private String msg;

    LoginResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }
}
