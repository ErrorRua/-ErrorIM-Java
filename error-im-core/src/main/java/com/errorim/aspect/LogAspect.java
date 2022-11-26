package com.errorim.aspect;


import com.alibaba.fastjson2.JSON;
import com.errorim.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
@Slf4j
public class LogAspect {

    //对哪些方法进行增强，切点表达式和注解两种方式
    @Pointcut("@annotation(com.errorim.annotation.SystemLog)")
    public void pt() {

    }

    //怎么增强，这里采用环绕增强
    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret;
        try {
            handleBefore(joinPoint);
            ret = joinPoint.proceed();
            handleAfter(ret);
        } finally {
            //系统换行符
            log.info("=======end=======" + System.lineSeparator());
        }
        return ret;
    }

    private void handleAfter(Object ret) {
        //打印出参
        log.info("Response      :{}", JSON.toJSON(ret));
        log.info("Time          :{}", new Date());

//        log.info("Response      :{}", ret); 不序列化返回：com.platform.entity.ResponseResult@7f91c404
    }

    private void handleBefore(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取被增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);

        log.info("====Start====");
        log.info("Class Method  :{}. {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        System.out.println(Arrays.toString(joinPoint.getArgs()));
        log.info("Time          :{}", new Date());

    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = methodSignature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;
    }
}
