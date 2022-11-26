package com.errorim.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * @author ErrorRua
 * @date 2022/11/21
 * @description:
 */
@Data
public class RegisterDTO {
    // 邮箱作为唯一标识

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 15, message = "密码长度必须在6-15之间")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "邮箱验证码不能为空")
    private String verifyEmailCode;

}
