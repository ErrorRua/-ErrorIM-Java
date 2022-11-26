package com.errorim.service.impl;


import com.errorim.entity.ResponseResult;
import com.errorim.exception.ErrorImException;
import com.errorim.mapper.UserMapper;
import com.errorim.service.EmailService;
import com.errorim.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.errorim.enums.UserCodeEnum.*;

/**
 * @author ErrorRua
 * @date 2022/11/18
 * @description:
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult sendVerifyCode(String email) {
        // 0.防止恶意发送
        if (Objects.nonNull(redisCache.getCacheObject(email))) {
            redisCache.deleteObject(email);
        }

        // 1.生成验证码
        String code = generateCode(6);
        Context context = new Context();
        context.setVariable("code", code);

        // 2.发送邮件
        String text = templateEngine.process("emailVerifyCode", context);

        // 3.保存验证码
        redisCache.setCacheObject(email, code, 10, TimeUnit.MINUTES);

        sendEmail(email, "您的Error-IM验证码", text);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult sendEmail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            //邮件发件人
            helper.setFrom(sendMailer);
            //邮件收件人
            helper.setTo(to);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new ErrorImException(EMAIL_SEND_ERROR.getCode(), EMAIL_SEND_ERROR.getMessage());
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult verifyEmail(String email, String verifyCode) {
        String emailCode = redisCache.getCacheObject(email);
        if (Objects.isNull(emailCode)) {
            throw new ErrorImException(EMAIL_VERIFY_CODE_EXPIRED.getCode(), EMAIL_VERIFY_CODE_EXPIRED.getMessage());
        }
        if (!emailCode.equalsIgnoreCase(verifyCode)) {
            throw new ErrorImException(EMAIL_VERIFY_CODE_ERROR.getCode(), EMAIL_VERIFY_CODE_ERROR.getMessage());
        }
        redisCache.deleteObject(email);
        return ResponseResult.okResult();
    }

    private String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }
}
