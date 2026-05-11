package com.CourierManagement.AdminService.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder // used to implement the Builder design pattern, allowing object creation in a readable and flexible way without relying on constructors.
@Data
@Table(name="delivery_monitor")
public class DeliveryMonitor {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name = "delivery_id", nullable = false, unique = true)
	private String deliveryId;
	@Column(name = "tracking_number", nullable = false)
	private String trackingNumber;
	private String customerName;
	private String senderCity;
	private String recieverCity;
	private DeliveryStatus currentStatus;
	private String assignedHub;
	
	@UpdateTimestamp
	@Column(name = "last_updated")
	private LocalDateTime lastUpdated;
	private LocalDateTime createdAt;
	@PrePersist // jpa lifecycle callback , run automatically before entity is saved in the database
	public void prePersist() {
		this.createdAt=LocalDateTime.now();
		
		
	}
	
	
	
}
