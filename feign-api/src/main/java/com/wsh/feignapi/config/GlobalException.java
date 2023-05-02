package com.wsh.feignapi.config;

import com.wsh.feignapi.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler()
    public Result handleRuntimeException(RuntimeException exception) {
        log.error(exception.toString(), exception);
        return Result.error("服务器异常");
    }
}
