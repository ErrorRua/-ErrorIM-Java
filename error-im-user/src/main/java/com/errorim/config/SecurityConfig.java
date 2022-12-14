package com.errorim.config;


import com.errorim.filter.JsonFilter;
import com.errorim.filter.JwtAuthenticationTokenFilter;
import com.errorim.filter.VerifyFilter;
import com.errorim.handler.AccessDeniedHandlerImpl;
import com.errorim.handler.AuthenticationEntryPointImpl;
import com.errorim.security.EmailCodeAuthenticationProvider;
import com.errorim.security.EmailPasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public EmailPasswordAuthenticationFilter emailCodeAuthenticationFilter(AuthenticationManager authenticationManager) {
        EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter = new EmailPasswordAuthenticationFilter(authenticationManager);
//		emailCodeAuthenticationFilter.setAuthenticationSuccessHandler(AuthenticationSuccessHandler);
//		emailCodeAuthenticationFilter.setAuthenticationFailureHandler(AuthenticationFailureHandler);
        return emailPasswordAuthenticationFilter;
    }

//	@Bean
//	public EmailCodeAuthenticationProvider emailCodeAuthenticationProvider(UserDetailsService userDetailsService) {
//		return new EmailCodeAuthenticationProvider(userDetailsService);
//	}


    @Bean
    SecurityFilterChain securityFilterChain
            (HttpSecurity http,
             JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter,
             JsonFilter jsonFilter,
             VerifyFilter verifyFilter,
             AuthenticationEntryPointImpl authenticationEntryPoint,
             AccessDeniedHandlerImpl accessDeniedHandler,
            UserDetailsService userDetailsService,
             EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter,
             EmailCodeAuthenticationProvider emailCodeAuthenticationProvider) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login").anonymous()
                .antMatchers("/user/register").anonymous()
                .antMatchers("/user/forgetPassword").anonymous()
                .antMatchers("/user/get-code").permitAll()
                .antMatchers("/user/logout").permitAll()
                .antMatchers("/error/exthrow").permitAll()
                .antMatchers("/user/get-email-code").permitAll()
                .antMatchers("/user/verify-email").permitAll()
                .antMatchers("/websocket").permitAll()
                .antMatchers("/websocket/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
//				.anyRequest().permitAll();

        http.authenticationProvider(emailCodeAuthenticationProvider);

        http.addFilterBefore(emailPasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //校验过滤器添加到过滤器链中
        http.addFilterBefore(jsonFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(verifyFilter, UsernamePasswordAuthenticationFilter.class);


        //添加认证/权限异常处理
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);

        //允许跨域
        http.cors();
        return http.build();
    }

}
