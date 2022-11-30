package com.errorim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.errorim.annotation.SystemLog;
import com.errorim.dto.RegisterDTO;
import com.errorim.dto.UpdateInfoDTO;
import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;
import com.errorim.exception.ErrorImException;
import com.errorim.mapper.UserMapper;
import com.errorim.service.EmailService;
import com.errorim.service.UserService;
import com.errorim.util.BeanCopyUtils;
import com.errorim.util.RedisCache;
import com.errorim.util.SecurityUtils;
import com.errorim.vo.UserInfoVO;
import com.errorim.vo.VerifyCodeVO;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.errorim.enums.UserCodeEnum.*;

/**
 * @author ErrorRua
 * @date 2022/11/22
 * @description:
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public ResponseResult register(RegisterDTO registerDTO) {

        // 1.判断邮箱是否已经注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, registerDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        if (Objects.nonNull(user)) {
            throw new ErrorImException(EMAIL_EXIST.getCode(), EMAIL_EXIST.getMessage());
        }

        // 2.判断验证码是否正确
        String emailCode = redisCache.getCacheObject(registerDTO.getEmail());
        redisCache.deleteObject(registerDTO.getEmail());
        if (Objects.isNull(emailCode)) {
            throw new ErrorImException(EMAIL_VERIFY_CODE_EXPIRED.getCode(), EMAIL_VERIFY_CODE_EXPIRED.getMessage());
        }
        if (!emailCode.equalsIgnoreCase(registerDTO.getVerifyEmailCode())) {
            throw new ErrorImException(EMAIL_VERIFY_CODE_ERROR.getCode(), EMAIL_VERIFY_CODE_ERROR.getMessage());
        }

        // 3. 存入数据库
        user = BeanCopyUtils.copyBean(registerDTO, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(registerDTO.getPassword()));
        int ret = userMapper.insert(user);

        if (ret == 0) {
            throw new ErrorImException(REGISTER_ERROR.getCode(), REGISTER_ERROR.getMessage());
        }

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getVerifyCode() {
        Captcha captcha = new SpecCaptcha(130, 48, 4);
        // 设置字体，有默认字体，可以不用设置
//        captcha.setFont(Captcha.FONT_1);
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);

        // 将uuid作为redis的key
        String key = UUID.randomUUID().toString();
        String code = captcha.text().toLowerCase();

        // 将验证码存入redis
        redisCache.setCacheObject(key, code, 5, TimeUnit.MINUTES);

        VerifyCodeVO verifyCodeVO = new VerifyCodeVO();
        verifyCodeVO.setVerifyKey(key);
        verifyCodeVO.setCodeImg(captcha.toBase64());

        return ResponseResult.okResult(verifyCodeVO);
    }

    @Override
    public ResponseResult getUserInfo() {
        String userId = SecurityUtils.getUserId();
        User user = userMapper.selectById(userId);

        UserInfoVO userInfoVO = BeanCopyUtils.copyBean(user, UserInfoVO.class);

        return ResponseResult.okResult(userInfoVO);
    }

    @Override
    public ResponseResult<User> getUserInfoByUserId(String userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);

        User user = userMapper.selectOne(queryWrapper);

        if (Objects.isNull(user)) {
            throw new ErrorImException(USER_NOT_EXIST.getCode(), USER_NOT_EXIST.getMessage());
        }

        UserInfoVO userInfoVO = BeanCopyUtils.copyBean(user, UserInfoVO.class);

        return ResponseResult.okResult(userInfoVO);
    }

    @Override
    public ResponseResult updateUserInfo(UpdateInfoDTO updateInfoDTO) {
        String userId = SecurityUtils.getUserId();

        User user = BeanCopyUtils.copyBean(updateInfoDTO, User.class);

        user.setUserId(userId);
        if (Objects.nonNull(updateInfoDTO.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(updateInfoDTO.getPassword()));
        }

        int ret = userMapper.updateById(user);
        if (ret == 0) {
            throw new ErrorImException(USER_UPDATE_ERROR.getCode(), USER_UPDATE_ERROR.getMessage());
        }

        return ResponseResult.okResult();

    }

    @Override
    public ResponseResult forgetPassword() {
        return null;
    }


}
