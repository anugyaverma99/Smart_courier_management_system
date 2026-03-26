package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.CourierManagement.AdminService.Dto.HubRequest;
import com.CourierManagement.AdminService.Dto.HubResponse;
import com.CourierManagement.AdminService.Entity.Hub;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.HubRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubService {

 private final HubRepository repository;

 
 public HubResponse createHub(HubRequest request) {
     Hub hub = Hub.builder()
             .name(request.getName())
             .city(request.getCity())
             .state(request.getState())
             .pincode(request.getPincode())
             .contactNumber(request.getContactNumber())
             .build();

     return toResponse(repository.save(hub));
 }


 public List<HubResponse> getActiveHubs() {
     return repository.findByActiveTrue()
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }

 
 public List<HubResponse> getAllHubs() {
     return repository.findAll()
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }

 
 public HubResponse deactivateHub(Long hubId) {
     Hub hub = repository.findById(hubId)
             .orElseThrow(() -> new AdminServiceException(
                     "Hub not found: " + hubId));

     hub.setActive(false);
     return toResponse(repository.save(hub));
 }

 private HubResponse toResponse(Hub h) {
     return HubResponse.builder()
             .id(h.getId())
             .name(h.getName())
             .city(h.getCity())
             .state(h.getState())
             .pincode(h.getPincode())
             .contactNumber(h.getContactNumber())
             .active(h.isActive())
             .createdAt(h.getCreatedAt())
             .build();
 }
}