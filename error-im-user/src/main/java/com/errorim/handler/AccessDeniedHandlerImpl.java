package com.errorim.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

		request.setAttribute("filter.error", new AccessDeniedException("访问被拒绝"));
		log.error(accessDeniedException.getMessage());
//		request.setAttribute("filter.error", new RuntimeException("访问被拒绝", accessDeniedException));

		//将异常分发到/error/exthrow控制器
		request.getRequestDispatcher("/error/exthrow").forward(request, response);
	}
}
