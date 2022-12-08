package com.errorim.enums;

import com.errorim.constants.BaseCode;

/**
 * @author ErrorRua
 * @date 2022/12/02
 * @description:
 */
public enum ContactEnum implements BaseCode {
    // 已经是好友
    IS_FRIEND(500, "对方已经是好友了"),
    // 不能添加自己为好友
    CAN_NOT_ADD_SELF(501, "不能添加自己为好友"),
    ;

    private final Integer code;
    private final String message;

    ContactEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
