package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.CourierManagement.AdminService.Dto.ExceptionRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResolveRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResponse;
import com.CourierManagement.AdminService.Entity.DeliveryException;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.DeliveryExceptionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Dto.DeliveryDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.CourierManagement.AdminService.Config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class ExceptionHandlingService {

    private final DeliveryExceptionRepository repository;
    private final DeliveryClient deliveryClient;
    private final RabbitTemplate rabbitTemplate;

    public List<ExceptionResponse> getOpenExceptions() {
        return repository.findByResolutionStatus(ExceptionStatus.OPEN)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ExceptionResponse> getAllExceptions() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

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
                    "Exception already resolved: " + exceptionId,409);
        }

        exception.setResolutionStatus(ExceptionStatus.RESOLVED);
        exception.setRemarks(request.getRemarks());
        exception.setResolvedBy(request.getResolvedBy());
        exception.setResolvedAt(LocalDateTime.now());

        DeliveryException savedException = repository.save(exception);
        sendEmailNotification(savedException, "EXCEPTION RESOLVED", request.getRemarks());
        return toResponse(savedException);
    }

    public ExceptionResponse raiseException(ExceptionRequest request) {
        DeliveryException exception = DeliveryException.builder()
                .deliveryId(request.getDeliveryId())
                .trackingNumber(request.getTrackingNumber())
                .exceptionStatus(request.getExceptionStatus())
                .resolutionStatus(ExceptionStatus.OPEN)
                .reason(request.getReason())
                .build();

        DeliveryException savedException = repository.save(exception);
        sendEmailNotification(savedException, request.getExceptionStatus().toString(), request.getReason());
        return toResponse(savedException);
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

    private void sendEmailNotification(DeliveryException exception, String status, String remarks) {
        try {
            DeliveryDto delivery = deliveryClient.getDeliveryById(exception.getDeliveryId());
            if (delivery != null && delivery.getReceiverAddress() != null) {
                String email = (String) delivery.getReceiverAddress().get("email");
                String name = (String) delivery.getReceiverAddress().get("name");

                if (email != null && !email.isEmpty()) {
                    Map<String, Object> notificationEvent = Map.of(
                            "trackingNumber", exception.getTrackingNumber(),
                            "status", status,
                            "customerEmail", email,
                            "customerName", name != null ? name : "Customer",
                            "remarks", remarks != null ? remarks : ""
                    );
                    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, notificationEvent);
                }
            }
        } catch (Exception e) {
            // Log the error but do not fail the main transaction
            System.err.println("Failed to publish exception email notification: " + e.getMessage());
        }
    }
}