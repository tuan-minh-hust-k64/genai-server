package com.genai.server.exception.handler;

import com.genai.server.common.exception.ErrorCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorCommon handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorCommon.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected err!")
                .build();
    }
//    @ResponseBody
//    @ExceptionHandler(value = {Exception.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorCommon handleException(Exception exception) {
//        log.error(exception.getMessage(), exception);
//        return ErrorCommon.builder()
//                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
//                .message("Unexpected err!")
//                .build();
//    }
}
