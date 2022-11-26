package com.errorim.controller;

import com.errorim.dto.LoginDTO;
import com.errorim.dto.RegisterDTO;
import com.errorim.dto.UpdateInfoDTO;
import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;
import com.errorim.service.EmailService;
import com.errorim.service.LoginService;
import com.errorim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author ErrorRua
 * @date 2022/11/20
 * @description:
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

    @Autowired
    private EmailService emailService;

    /**
     * @description: 登录
     * @param loginDTO:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/23
     */
    @PostMapping("/login")
    public ResponseResult login(@Valid @RequestBody LoginDTO loginDTO){
        return loginService.login(loginDTO);
    }

    /**
     * @description: 登出
     * @param request:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/23
     */
    @GetMapping("/logout")
    public ResponseResult logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        return loginService.logout(token);
    }

    /**
     * @description: 注册
     * @param registerDTO:
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/23
     */
    @PostMapping("/register")
    public ResponseResult register(@Valid @RequestBody RegisterDTO registerDTO){
        return userService.register(registerDTO);
    }

    /**
     * @description: 获取用户信息

     * @return com.errorim.entity.ResponseResult<com.errorim.entity.User> 用户信息
     * @author ErrorRua
     * @date 2022/11/23
     */
    @GetMapping("/info")
    public ResponseResult<User> getUserInfo(){
        return userService.getUserInfo();
    }

    /**
     * @description: 通过id获取用户信息
     * @param userId:
     * @return com.errorim.entity.ResponseResult<com.errorim.entity.User> 用户信息
     * @author ErrorRua
     * @date 2022/11/23
     */
    @GetMapping("/info/{userId}")
    public ResponseResult<User> getUserInfoByUserId(@PathVariable String userId){
        return userService.getUserInfoByUserId(userId);
    }

    /**
     * @description: 修改用户信息
     * @param updateInfoDTO:
     * @return com.errorim.entity.ResponseResult<com.errorim.entity.User> 用户信息
     * @author ErrorRua
     * @date 2022/11/23
     */
    @PutMapping("/info")
    public ResponseResult<User> updateUserInfo(@Valid @RequestBody UpdateInfoDTO updateInfoDTO){
        return userService.updateUserInfo(updateInfoDTO);
    }

    /**
     * @description: 忘记密码
     * @param :
     * @return com.errorim.entity.ResponseResult<com.errorim.entity.User> 用户信息
     * @author ErrorRua
     * @date 2022/11/23
     */
    @PutMapping("/forgetPassword")
    public ResponseResult forgetPassword(){
        return userService.forgetPassword();
    }

    /**
     * @description: 获取验证码
     * @return com.errorim.entity.ResponseResult
     * @author ErrorRua
     * @date 2022/11/23
     */
    @GetMapping("/get-code")
    public ResponseResult getVerifyCode(){
        return userService.getVerifyCode();
    }

    @GetMapping("/get-email-code")
    public ResponseResult getEmailVerifyCode(String email){
        return emailService.sendVerifyCode(email);
    }

    @PostMapping("/verify-email")
    public ResponseResult verifyEmail(String email,String verifyCode){
        return emailService.verifyEmail(email,verifyCode);
    }
}
