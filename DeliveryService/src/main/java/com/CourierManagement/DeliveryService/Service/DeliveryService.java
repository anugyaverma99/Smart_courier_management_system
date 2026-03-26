
package com.CourierManagement.DeliveryService.Service;

import com.CourierManagement.DeliveryService.Dto.AddressDto;
import com.CourierManagement.DeliveryService.Dto.CreateDeliveryRequest;
import com.CourierManagement.DeliveryService.Dto.DeliveryResponse;
import com.CourierManagement.DeliveryService.Dto.PackageDto;
import com.CourierManagement.DeliveryService.Dto.UpdateStatusRequest;
import com.CourierManagement.DeliveryService.Entity.Address;
import com.CourierManagement.DeliveryService.Entity.Delivery;
import com.CourierManagement.DeliveryService.Entity.DeliveryStatus;
import com.CourierManagement.DeliveryService.Entity.PackageDetails;
import com.CourierManagement.DeliveryService.Exception.DeliveryServiceException;
import com.CourierManagement.DeliveryService.Repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repository;

    
    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {

        
        Address sender = Address.builder()
                .name(request.getSenderAddress().getName())
                .phone(request.getSenderAddress().getPhone())
                .addressLine(request.getSenderAddress().getAddressLine())
                .city(request.getSenderAddress().getCity())
                .state(request.getSenderAddress().getState())
                .zipCode(request.getSenderAddress().getZipCode())
                .country(request.getSenderAddress().getCountry())
                .build();

        
        Address receiver = Address.builder()
                .name(request.getReceiverAddress().getName())
                .phone(request.getReceiverAddress().getPhone())
                .addressLine(request.getReceiverAddress().getAddressLine())
                .city(request.getReceiverAddress().getCity())
                .state(request.getReceiverAddress().getState())
                .zipCode(request.getReceiverAddress().getZipCode())
                .country(request.getReceiverAddress().getCountry())
                .build();

        
        PackageDetails pkg = PackageDetails.builder()
                .description(request.getPackageDetails().getDescription())
                .weightKg(request.getPackageDetails().getWeightKg())
                .lengthCm(request.getPackageDetails().getLengthCm())
                .widthCm(request.getPackageDetails().getWidthCm())
                .heightCm(request.getPackageDetails().getHeightCm())
                .serviceType(request.getPackageDetails().getServiceType())
                .declaredValue(request.getPackageDetails().getDeclaredValue())
                .build();

        // Calculate charge based on weight and service type
        double charge = calculateCharge(
                request.getPackageDetails().getWeightKg(),
                request.getPackageDetails().getServiceType());

        // Generate unique tracking number
        String trackingNumber = "TRK-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        // Build delivery — starts at DRAFT status always
        Delivery delivery = Delivery.builder()
                .trackingNumber(trackingNumber)
                .customerId(request.getCustomerId())
                .senderAddress(sender)
                .receiverAddress(receiver)
                .packageDetails(pkg)
                .charge(charge)
                .pickupScheduledAt(request.getPickupScheduledAt())
                .status(DeliveryStatus.DRAFT)
                .build();

        return toResponse(repository.save(delivery));
    }

    
    public List<DeliveryResponse> getMyDeliveries(String customerId) {
        List<Delivery> deliveries =
                repository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        if (deliveries.isEmpty()) {
            throw new DeliveryServiceException(
                    "No deliveries found for customer: " + customerId);
        }
        return deliveries.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    
    public DeliveryResponse getById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new DeliveryServiceException(
                        "Delivery not found: " + id));
    }

    
    public DeliveryResponse getByTrackingNumber(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber)
                .map(this::toResponse)
                .orElseThrow(() -> new DeliveryServiceException(
                        "Delivery not found for tracking number: " + trackingNumber));
    }

    
    public DeliveryResponse updateStatus(Long id, UpdateStatusRequest request) {

        Delivery delivery = repository.findById(id)
                .orElseThrow(() -> new DeliveryServiceException(
                        "Delivery not found: " + id));

        
        validateStatusTransition(delivery.getStatus(), request.getStatus());

        delivery.setStatus(request.getStatus());
        return toResponse(repository.save(delivery));
    }

   
    private double calculateCharge(double weightKg, String serviceType) {
        double baseRate;

        switch (serviceType.toLowerCase()) {
            case "express":
                baseRate = 80.0;
                break;
            case "international":
                baseRate = 200.0;
                break;
            default: // domestic
                baseRate = 40.0;
                break;
        }
        
        return baseRate * Math.max(weightKg, 1.0);
    }

   
    private void validateStatusTransition(
            DeliveryStatus current, DeliveryStatus next) {

        List<DeliveryStatus> lifecycle = List.of(
                DeliveryStatus.DRAFT,
                DeliveryStatus.BOOKED,
                DeliveryStatus.PICKED_UP,
                DeliveryStatus.IN_TRANSIT,
                DeliveryStatus.OUT_FOR_DELIVERY,
                DeliveryStatus.DELIVERED
        );

       
        List<DeliveryStatus> exceptionStates = List.of(
                DeliveryStatus.DELAYED,
                DeliveryStatus.FAILED,
                DeliveryStatus.RETURNED
        );

        if (exceptionStates.contains(next)) return;

        int currentIndex = lifecycle.indexOf(current);
        int nextIndex    = lifecycle.indexOf(next);

        if (nextIndex <= currentIndex) {
            throw new DeliveryServiceException(
                    "Invalid status transition from "
                    + current + " to " + next);
        }
    }

   
    private DeliveryResponse toResponse(Delivery d) {

        AddressDto senderDTO = AddressDto.builder()
                .name(d.getSenderAddress().getName())
                .phone(d.getSenderAddress().getPhone())
                .addressLine(d.getSenderAddress().getAddressLine())
                .city(d.getSenderAddress().getCity())
                .state(d.getSenderAddress().getState())
                .zipCode(d.getSenderAddress().getZipCode())
                .country(d.getSenderAddress().getCountry())
                .build();

        AddressDto receiverDTO = AddressDto.builder()
                .name(d.getReceiverAddress().getName())
                .phone(d.getReceiverAddress().getPhone())
                .addressLine(d.getReceiverAddress().getAddressLine())
                .city(d.getReceiverAddress().getCity())
                .state(d.getReceiverAddress().getState())
                .zipCode(d.getReceiverAddress().getZipCode())
                .country(d.getReceiverAddress().getCountry())
                .build();

        PackageDto packageDTO = PackageDto.builder()
                .description(d.getPackageDetails().getDescription())
                .weightKg(d.getPackageDetails().getWeightKg())
                .lengthCm(d.getPackageDetails().getLengthCm())
                .widthCm(d.getPackageDetails().getWidthCm())
                .heightCm(d.getPackageDetails().getHeightCm())
                .serviceType(d.getPackageDetails().getServiceType())
                .declaredValue(d.getPackageDetails().getDeclaredValue())
                .build();

        return DeliveryResponse.builder()
                .id(d.getId())
                .trackingNumber(d.getTrackingNumber())
                .customerId(d.getCustomerId())
                .senderAddress(senderDTO)
                .receiverAddress(receiverDTO)
                .packageDetails(packageDTO)
                .status(d.getStatus())
                .charge(d.getCharge())
                .pickupScheduledAt(d.getPickupScheduledAt())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}