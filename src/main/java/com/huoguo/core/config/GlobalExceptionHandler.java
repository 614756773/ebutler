package com.huoguo.core.config;

import com.huoguo.model.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Result> handleException(Exception e) {
        log.error(e.getMessage(), e);
        Result<Object> fail = Result.error(e.getMessage());
        return new ResponseEntity<>(fail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}