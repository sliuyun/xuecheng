package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description 全局异常处理器
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获自定义异常
     * @param e 自定义异常
     * @return  RestErrorResponse
     */
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {
        log.error("{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getMessage());
    }

    /**
     * 捕获非法参数异常
     * @param e 非法参数异常
     * @return  RestErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorResponse illegalArgument(MethodArgumentNotValidException e) {
        // 获取错误校验结果
        BindingResult bindingResult = e.getBindingResult();
        // 错误信息列表
        List<String> errList = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errList.add(fieldError.getDefaultMessage());
        }
        // 错误信息
        String errStr = Arrays.toString(errList.toArray());
        log.error("{}",errStr,e);
        return new RestErrorResponse(errStr);
    }

    /**
     * 捕获系统异常
     * @param e 系统异常
     * @return  RestErrorResponse
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse systemException(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(e.getMessage());
    }
}
