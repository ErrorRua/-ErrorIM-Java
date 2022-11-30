package com.errorim.enums;

import com.errorim.constants.BaseCode;

/**
 * @author ErrorRua
 * @date 2022/12/01
 * @description:
 */
public enum UploadEnum implements BaseCode {
    // 上传头像失败
    UPLOAD_AVATAR_FAIL(500, "上传头像失败"),
    // 不是图片文件
    NOT_IMAGE_FILE(501, "不是图片文件")
    ;


    private final Integer code;
    private final String message;

    UploadEnum(Integer code, String message) {
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
