package com.gxdcxmplex.backend.player;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception){
        Map<String, String> fields = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("fields", fields);
        return ResponseEntity.badRequest().body(response);
    }
}
