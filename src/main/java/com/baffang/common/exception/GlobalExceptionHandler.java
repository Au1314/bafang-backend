package com.baffang.common.exception;

import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException e, HttpServletResponse response) {
        log.error("异常信息：{}", e.getMessage());

        // 根据业务错误码设置HTTP响应状态码
        switch (e.getCode()) {
            case 2000: // UNAUTHORIZED
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                break;
            case 2003: // FORBIDDEN
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理方法校验异常
     * 
     * @param e 参数校验错误
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result argumentValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            FieldError fieldError = (FieldError) error;
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return Result.error(ErrorMsg.ARGUMENT_NOT_VALID.getCode(), ErrorMsg.ARGUMENT_NOT_VALID.getMsg(), errors);
    }

    /**
     * 捕获MYSQL语句异常
     * 安全整改：SQL 细节（错误描述/SQLState/错误码）只进服务端日志，不再回传客户端
     *
     * @param e SQL异常
     * @return
     */
    @ExceptionHandler(SQLException.class)
    public Result sqlExceptionHandler(SQLException e, HttpServletResponse response) {
        log.error("SQL异常: {}", e.getMessage(), e);
        SQLException currentEx = e.getNextException();
        int exIndex = 1;
        while (currentEx != null) {
            log.error("SQL异常链[{}]: message={}, sqlState={}, errorCode={}",
                    exIndex++, currentEx.getMessage(), currentEx.getSQLState(), currentEx.getErrorCode());
            currentEx = currentEx.getNextException();
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return Result.error(ErrorMsg.SQL_EXCEPTION.getCode(), ErrorMsg.SQL_EXCEPTION.getMsg());
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result accessDeniedExceptionHandler(AccessDeniedException e, HttpServletResponse response) {
        log.error("访问拒绝异常：{}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return Result.error(ErrorMsg.FORBIDDEN);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result authenticationExceptionHandler(AuthenticationException e, HttpServletResponse response) {
        log.error("认证异常：{}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 根据具体异常类型返回更精确的错误信息
        if (e instanceof BadCredentialsException) {
            return Result.error(ErrorMsg.PASSWORD_ERROR);
        } else if (e instanceof UsernameNotFoundException) {
            return Result.error(ErrorMsg.ACCOUNT_NOT_FOUND);
        } else {
            return Result.error(ErrorMsg.LOGIN_FAILED);
        }
    }

    /**
     * 兜底：未预期的异常统一返回 500，不向客户端泄露内部细节
     */
    @ExceptionHandler(Exception.class)
    public Result unexpectedExceptionHandler(Exception e, HttpServletResponse response) {
        log.error("未处理异常: {}", e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return Result.error(500, "服务器内部错误，请稍后重试");
    }

}
