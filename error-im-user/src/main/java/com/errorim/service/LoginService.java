package com.errorim.service;


import com.errorim.dto.LoginDTO;
import com.errorim.entity.ResponseResult;
import com.errorim.entity.User;

public interface LoginService {
	ResponseResult login(LoginDTO loginDTO);

	ResponseResult logout(String token);
}
