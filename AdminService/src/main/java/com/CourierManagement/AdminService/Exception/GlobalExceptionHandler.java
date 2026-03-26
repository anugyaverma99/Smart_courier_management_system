package com.CourierManagement.AdminService.Exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(AdminServiceException.class)
 public ResponseEntity<Map<String, Object>> handleAdminException(
         AdminServiceException ex) {
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
