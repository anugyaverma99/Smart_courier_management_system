package com.CourierManagement.DeliveryService.Service;
import org.springframework.stereotype.Service;

import com.CourierManagement.DeliveryService.Entity.Delivery;
import com.CourierManagement.DeliveryService.Entity.Status;
import com.CourierManagement.DeliveryService.Repository.DeliveryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository repository;

    public DeliveryService(DeliveryRepository repository) {
        this.repository = repository;
    }

    // Create delivery
    public Delivery createDelivery(Delivery delivery) {
        delivery.setStatus(Status.DRAFT);
        delivery.setBookedAt(LocalDateTime.now());
        return repository.save(delivery);
    }

    // Get all deliveries of a customer
    public List<Delivery> getDeliveriesByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    // Get delivery by ID
    public Delivery getDelivery(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
    }

    // Update delivery status
    public Delivery updateStatus(Long id, Status newStatus) {
        Delivery delivery = getDelivery(id);
        Status current = delivery.getStatus();

        switch (current) {
            case DRAFT:
                if (newStatus == Status.BOOKED) 
                    delivery.setBookedAt(LocalDateTime.now());
                else 
                    throw new RuntimeException("DRAFT can only go to BOOKED");
                break;

            case BOOKED:
                if (newStatus == Status.PICKED_UP) 
                    delivery.setPickedUpAt(LocalDateTime.now());
                else if (newStatus != Status.DELAYED && newStatus != Status.FAILED)
                    throw new RuntimeException("BOOKED can only go to PICKED_UP, DELAYED, or FAILED");
                break;

            case PICKED_UP:
                if (newStatus != Status.IN_TRANSIT && newStatus != Status.DELAYED && newStatus != Status.FAILED)
                    throw new RuntimeException("PICKED_UP can only go to IN_TRANSIT, DELAYED, or FAILED");
                break;

            case IN_TRANSIT:
                if (newStatus != Status.OUT_FOR_DELIVERY && newStatus != Status.DELAYED && newStatus != Status.FAILED)
                    throw new RuntimeException("IN_TRANSIT can only go to OUT_FOR_DELIVERY, DELAYED, or FAILED");
                break;

            case OUT_FOR_DELIVERY:
                if (newStatus == Status.DELIVERED) 
                    delivery.setDeliveredAt(LocalDateTime.now());
                else if (newStatus != Status.DELAYED && newStatus != Status.FAILED)
                    throw new RuntimeException("OUT_FOR_DELIVERY can only go to DELIVERED, DELAYED, or FAILED");
                break;

            case DELAYED:
                if (newStatus != Status.IN_TRANSIT && newStatus != Status.OUT_FOR_DELIVERY && newStatus != Status.FAILED)
                    throw new RuntimeException("DELAYED can only go to IN_TRANSIT, OUT_FOR_DELIVERY, or FAILED");
                break;

            case FAILED:
                if (newStatus != Status.RETURNED)
                    throw new RuntimeException("FAILED can only go to RETURNED");
                break;

            case DELIVERED:
            case RETURNED:
                throw new RuntimeException(current + " cannot be updated further");
        }

        delivery.setStatus(newStatus);
        return repository.save(delivery);
    }
}