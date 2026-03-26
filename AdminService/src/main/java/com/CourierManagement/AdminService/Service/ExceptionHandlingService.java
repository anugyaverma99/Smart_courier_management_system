package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.CourierManagement.AdminService.Dto.ExceptionResolveRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResponse;
import com.CourierManagement.AdminService.Entity.DeliveryException;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.DeliveryExceptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExceptionHandlingService {

 private final DeliveryExceptionRepository repository;


 public List<ExceptionResponse> getOpenExceptions() {
     return repository.findByResolutionStatus(ExceptionStatus.OPEN)
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }

 // All exceptions regardless of status
 public List<ExceptionResponse> getAllExceptions() {
     return repository.findAll()
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }

 // Exceptions for one specific delivery
 public List<ExceptionResponse> getByDeliveryId(String deliveryId) {
     return repository.findByDeliveryId(deliveryId)
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }


 public ExceptionResponse resolveException(
         Long exceptionId, ExceptionResolveRequest request) {

     DeliveryException exception = repository.findById(exceptionId)
             .orElseThrow(() -> new AdminServiceException(
                     "Exception not found: " + exceptionId));

     
     if (exception.getResolutionStatus() == ExceptionStatus.RESOLVED) {
         throw new AdminServiceException(
                 "Exception already resolved: " + exceptionId);
     }

     exception.setResolutionStatus(ExceptionStatus.RESOLVED);
     exception.setRemarks(request.getRemarks());
     exception.setResolvedBy(request.getResolvedBy());
     exception.setResolvedAt(LocalDateTime.now());

     return toResponse(repository.save(exception));
 }

 private ExceptionResponse toResponse(DeliveryException e) {
     return ExceptionResponse.builder()
             .id(e.getId())
             .deliveryId(e.getDeliveryId())
             .trackingNumber(e.getTrackingNumber())
             .exceptionStatus(e.getExceptionStatus())
             .resolutionStatus(e.getResolutionStatus())
             .reason(e.getReason())
             .remarks(e.getRemarks())
             .resolvedBy(e.getResolvedBy())
             .raisedAt(e.getRaisedAt())
             .resolvedAt(e.getResolvedAt())
             .build();
 }
}