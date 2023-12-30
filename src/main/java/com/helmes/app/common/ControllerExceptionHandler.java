package com.helmes.app.common;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.helmes.app.industry.exception.BusinessLogicException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            errors.add(fieldError.getDefaultMessage());
        });

        bindingResult.getGlobalErrors().forEach(globalError -> {
            errors.add(globalError.getDefaultMessage());
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation error")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .errors(Collections.singletonList(ex.getMessage()))
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Builder @Value
    public static class ErrorResponse {
        int status;
        String message;
        List<String> errors;
    }
}