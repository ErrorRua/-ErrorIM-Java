package com.errorim.controller;

import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;
import com.errorim.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ErrorRua
 * @date 2022/11/20
 * @description:
 */

@RestController
@RequestMapping("/user")
public class UserController {
//    @Autowired
//    private UserService userService;
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        return loginService.login(user);
    }
}
