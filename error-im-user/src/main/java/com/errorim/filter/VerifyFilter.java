package com.errorim.filter;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.errorim.entity.ResponseResult;
import com.errorim.exception.ErrorImException;
import com.errorim.util.RedisCache;
import com.errorim.wrapper.RequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.errorim.enums.UserCodeEnum.VERIFY_CODE_ERROR;
import static com.errorim.enums.UserCodeEnum.VERIFY_CODE_EXPIRED;

/**
 * @author ErrorRua
 * @date 2022/11/17
 * @description:
 */
@Component
public class VerifyFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        if (request.getRequestURI().equals("/user/login")
                && request.getMethod().equalsIgnoreCase("post")
            || request.getRequestURI().equals("/user/register")
                && request.getMethod().equalsIgnoreCase("post") ) {

            ServletInputStream inputStream = request.getInputStream();
            String s = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            JSONObject jsonObject = JSON.parseObject(s);
            String verifyCode = jsonObject.getString("verifyCode");
            String verifyKey = jsonObject.getString("verifyKey");

            if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(verifyKey)) {
                request.setAttribute("filter.error",
                        new ErrorImException(VERIFY_CODE_ERROR.getCode(), VERIFY_CODE_ERROR.getMessage()));
                //将异常分发到/error/exthrow控制器
                request.getRequestDispatcher("/error/exthrow").forward(request, response);
                return;
            }


            Object validateCode = redisCache.getCacheObject(verifyKey);
            redisCache.deleteObject(verifyKey);

            if (Objects.isNull(validateCode)) {
                request.setAttribute("filter.error",
                        new ErrorImException(VERIFY_CODE_EXPIRED.getCode(), VERIFY_CODE_EXPIRED.getMessage())           );
                //将异常分发到/error/exthrow控制器
                request.getRequestDispatcher("/error/exthrow").forward(request, response);
                return;
            }

            if (!validateCode.toString().equalsIgnoreCase(verifyCode)) {
                request.setAttribute("filter.error",
                        new ErrorImException(VERIFY_CODE_ERROR.getCode(), VERIFY_CODE_ERROR.getMessage()));
                //将异常分发到/error/exthrow控制器
                request.getRequestDispatcher("/error/exthrow").forward(request, response);
                return;
            }

        }


        filterChain.doFilter(request,response);
    }
}
