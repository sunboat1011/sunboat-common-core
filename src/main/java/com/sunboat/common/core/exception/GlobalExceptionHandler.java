package com.sunboat.common.core.exception;


import com.sunboat.common.core.enums.ResultCodeEnum;
import com.sunboat.common.core.result.RtnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public RtnResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("请求地址'{}'，业务异常：{}", request.getRequestURI(), e.getMessage());
        return RtnResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理请求参数验证失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RtnResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(";");
        }
        log.error("请求地址'{}'，参数验证失败：{}", request.getRequestURI(), errorMsg);
        return RtnResult.fail(ResultCodeEnum.DATA_VALIDATION_ERROR, errorMsg.toString());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public RtnResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String errorMsg = "参数'" + e.getName() + "'类型不匹配，期望类型：" + e.getRequiredType().getSimpleName();
        log.error("请求地址'{}'，{}", request.getRequestURI(), errorMsg);
        return RtnResult.fail(ResultCodeEnum.BAD_REQUEST, errorMsg);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public RtnResult<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("请求地址'{}'，发生未知异常：", request.getRequestURI(), e);
        return RtnResult.fail(ResultCodeEnum.INTERNAL_SERVER_ERROR, "系统异常，请联系管理员");
    }
}
    