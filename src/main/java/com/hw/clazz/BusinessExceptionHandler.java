package com.hw.clazz;

import com.hw.shared.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {GmailExpcetion.class})
    protected ResponseEntity<?> handleException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
