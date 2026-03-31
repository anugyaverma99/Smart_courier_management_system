package com.CourierManagement.AdminService.Exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice  //apply to all controllers
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
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Map<String, Object>> handleValidation(
         MethodArgumentNotValidException ex) {

     Map<String, String> errors = new HashMap<>();
     ex.getBindingResult().getAllErrors().forEach(error -> {
         String field = ((FieldError) error).getField();
         String message = error.getDefaultMessage();
         errors.put(field, message);
     });

     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
             "timestamp", LocalDateTime.now(),
             "status", 400,
             "errors", errors
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
