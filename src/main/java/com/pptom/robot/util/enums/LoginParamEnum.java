package com.pptom.robot.util.enums;

/**
 * @author: Mr Tom
 * @date: 2018/7/17
 * @since: 2018/7/17
 * @email: ptomjie@gmail.com
 * @description:
 */
public enum LoginParamEnum {
    /**
     *
     */
    LOGIN_ICON("loginicon", "true"),
    /**
     *
     */
    UUID("uuid", ""),
    /**
     *
     */
    TIP("tip", "0"),
    /**
     *
     */
    R("r", ""),
    /**
     *
     */
    _("_", "");

    private String name;
    private String value;

    LoginParamEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
