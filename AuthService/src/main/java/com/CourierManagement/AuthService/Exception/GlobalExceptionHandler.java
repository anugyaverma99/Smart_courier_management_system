package com.CourierManagement.AuthService.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(AuthServiceException.class)
 public ResponseEntity<Map<String, Object>> handleAuthException(
         AuthServiceException ex) {
     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
             "timestamp", LocalDateTime.now(),
             "status", 400,
             "error", ex.getMessage()
     ));
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
	 ex.printStackTrace();
     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
             "timestamp", LocalDateTime.now(),
             "status", 500,
             "error", "Something went wrong"
     ));
 }
}
