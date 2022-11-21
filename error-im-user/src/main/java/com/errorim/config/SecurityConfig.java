package com.errorim.config;


import com.errorim.filter.JwtAuthenticationTokenFilter;
import com.errorim.handler.AccessDeniedHandlerImpl;
import com.errorim.handler.AuthenticationEntryPointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 获取AuthenticationManager（认证管理器），登录时认证使用
	 * @param authenticationConfiguration
	 * @return
	 * @throws Exception
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain
			(HttpSecurity http,
			 JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter,
			 AuthenticationEntryPointImpl authenticationEntryPoint,
			 AccessDeniedHandlerImpl accessDeniedHandler
	) throws Exception {
		http
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				// 对于登录接口 允许匿名访问
				.antMatchers("/user/login").anonymous()
				// 除上面外的所有请求全部需要鉴权认证
//				.anyRequest().authenticated();
				.anyRequest().permitAll();

		//校验过滤器添加到过滤器链中
		http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

		//添加认证/权限异常处理
		http.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler)
				.authenticationEntryPoint(authenticationEntryPoint);

		//允许跨域
		http.cors();
		return http.build();
	}

}
