package com.CourierManagement.TrackingService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(TrackingNotFoundException.class)
 public ResponseEntity<Map<String, Object>> handleNotFound(TrackingNotFoundException ex) {
     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
             "timestamp", LocalDateTime.now(),
             "status", 404,
             "error", ex.getMessage()
     ));
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
             "timestamp", LocalDateTime.now(),
             "status", 500,
             "error", "Something went wrong"
     ));
 }
}
