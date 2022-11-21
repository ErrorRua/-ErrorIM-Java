package com.errorim.exception;


import com.errorim.entity.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.errorim.constants.SystemConstant.GLOBAL_EXCEPTION;
import static com.errorim.constants.SystemConstant.PARAMETER_VALIDATION;

/**
 * @author ErrorRua
 * @date 2022/11/19
 * @description:
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理参数校验异常
    @ExceptionHandler(value = BindException.class)
    public ResponseResult  handle(BindException e) {
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        // 获取message的值
        return ResponseResult.errorResult(HttpStatus.BAD_REQUEST.value(), defaultMessage);
    }

    //处理自定义异常
    @ExceptionHandler(ErrorImException.class)
    public ResponseResult exception(ErrorImException e) {
        log.error("出现了异常！{}", e.getMsg());
        return ResponseResult.errorResult(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception e) {
        log.error("异常：{}", e.getMessage());
        return ResponseResult.errorResult(GLOBAL_EXCEPTION, e.getMessage());
    }

}
