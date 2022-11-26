package com.errorim.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.errorim.entity.LoginUser;

import com.errorim.entity.User;
import com.errorim.exception.ErrorImException;
import com.errorim.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(User::getEmail, email);

		User user = userMapper.selectOne(queryWrapper);

		if(Objects.isNull(user)){
			throw new ErrorImException(HttpStatus.UNAUTHORIZED.value(),"邮箱或密码错误");
		}
		//TODO 根据用户查询权限信息 添加到LoginUser中

//		List<String> permissions = new ArrayList<>(Arrays.asList("test"));

//		List<String> permissions = menuMapper.selectPermsByUserId(user.getId());

		//封装成UserDetails对象返回
		return new LoginUser(user);
	}

//	@Autowired
//	private MenuMapper menuMapper;

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		//根据用户名查询用户信息
//		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//		queryWrapper.eq(User::getUsername, username);
//
//		User user = userMapper.selectOne(queryWrapper);
//
//		if(Objects.isNull(user)){
//			throw new ErrorImException(HttpStatus.UNAUTHORIZED.value(),"用户名或密码错误");
//		}
//		//TODO 根据用户查询权限信息 添加到LoginUser中
//
////		List<String> permissions = new ArrayList<>(Arrays.asList("test"));
//
////		List<String> permissions = menuMapper.selectPermsByUserId(user.getId());
//
//		//封装成UserDetails对象返回
//		return new LoginUser(user);
//	}
}
