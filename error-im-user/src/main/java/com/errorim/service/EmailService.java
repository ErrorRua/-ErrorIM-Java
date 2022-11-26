package com.errorim.service;


import com.errorim.entity.ResponseResult;

/**
 * @author ErrorRua
 * @date 2022/11/18
 * @description:
 */
public interface EmailService {

    /**
     * @description: 发送验证码
     * @param email:
     * @return com.platform.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/18
     */
    ResponseResult sendVerifyCode(String email);

    /**
     * @description: 发送邮件
     * @param email:
     * @param subject:
     * @param text:
     * @return void
     * @author ErrorRua
     * @date 2022/11/18
     */
    ResponseResult sendEmail(String email, String subject, String text);


    /**
     * @description: 检验邮箱
     * @param email:
     * @param verifyCode:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/23
     */
    ResponseResult verifyEmail(String email, String verifyCode);

}
