package com.CourierManagement.DeliveryService.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @Column(name = "customer_id", nullable = false)
    private String customerId; 

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_address_id")
    private Address senderAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_address_id")
    private Address receiverAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "package_id")
    private PackageDetails packageDetails;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;
    
    private double charge;
    
    private LocalDateTime pickupScheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    //Runs before an entity is saved (INSERT) into the database.
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = DeliveryStatus.DRAFT;
        }
    }
 // Runs before an entity is updated (UPDATE) in the database.
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}








