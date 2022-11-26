package com.errorim.filter;


import com.errorim.entity.LoginUser;
import com.errorim.exception.ErrorImException;
import com.errorim.util.JwtUtil;
import com.errorim.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.errorim.enums.UserCodeEnum.LOGIN_ERROR;
import static com.errorim.enums.UserCodeEnum.USER_NOT_LOGIN;


@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	private RedisCache redisCache;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


		//获取token
		String token = request.getHeader("Authorization");

		if (!StringUtils.hasText(token)) {
			//放行
			filterChain.doFilter(request, response);
			return;
		}

		//解析token
		String uuid;
		try {
			uuid = JwtUtil.parseJWT(token).getSubject();
		} catch (Exception e) {
			request.setAttribute("filter.error", new ErrorImException(LOGIN_ERROR.getCode(), LOGIN_ERROR.getMessage()));
			//将异常分发到/error/exthrow控制器
			request.getRequestDispatcher("/error/exthrow").forward(request, response);
			return;
		}

		//从redis中获取用户信息
		LoginUser loginUser = redisCache.getCacheObject(uuid);

		if (Objects.isNull(loginUser)) {
			// 异常捕获，发送到error controller
			request.setAttribute("filter.error", new ErrorImException(USER_NOT_LOGIN.getCode(), USER_NOT_LOGIN.getMessage()));
			//将异常分发到/error/exthrow控制器
			request.getRequestDispatcher("/error/exthrow").forward(request, response);
			return;
		}

		//存入SecurityContextHolder
		//TODO 获取权限信息封装到Authentication中
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);

		//放行
		filterChain.doFilter(request, response);
	}
}
