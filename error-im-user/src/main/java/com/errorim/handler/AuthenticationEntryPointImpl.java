package com.errorim.handler;

import com.errorim.exception.ErrorImException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		// TODO 用户名或密码错误

		ErrorImException errorImException = new ErrorImException(HttpStatus.UNAUTHORIZED.value(), "登录认证失败，请重新登录");
		request.setAttribute("filter.error", errorImException);
		//将异常分发到/error/exthrow控制器
		request.getRequestDispatcher("/error/exthrow").forward(request, response);
	}
}
