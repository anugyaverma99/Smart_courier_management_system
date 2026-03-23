package com.CourierManagement.DeliveryService.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId; 
 // Sender info
    private String senderName;
    private String senderPhone;
    private String senderStreet;
    private String senderCity;
    private String senderState;
    private String senderCountry;
    private String senderZip;

    // Receiver info
    private String receiverName;
    private String receiverPhone;
    private String receiverStreet;
    private String receiverCity;
    private String receiverState;
    private String receiverCountry;
    private String receiverZip;

    // Package info
    private String packageDetails;

    // Status & timestamps
    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;
    private LocalDateTime bookedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
}