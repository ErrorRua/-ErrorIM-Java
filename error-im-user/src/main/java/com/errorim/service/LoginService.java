package com.errorim.service;


import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;

public interface LoginService {
	ResponseResult login(User user);

	ResponseResult logout(String token);
}
