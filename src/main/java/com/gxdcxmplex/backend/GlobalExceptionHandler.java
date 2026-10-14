package com.gxdcxmplex.backend;

import com.gxdcxmplex.backend.player.InvalidCredentialsException;
import com.gxdcxmplex.backend.player.PlayerNotFoundException;
import com.gxdcxmplex.backend.player.UsernameTakenException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerNotFound(PlayerNotFoundException exception){
        Map<String, Object> response = new HashMap<>();
        response.put("error", exception.getMessage());

        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(IllegalArgumentException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameTaken(UsernameTakenException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }
}
