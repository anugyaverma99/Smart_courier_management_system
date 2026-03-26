package com.CourierManagement.TrackingService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.CourierManagement.TrackingService.Client.DeliveryClient;
import com.CourierManagement.TrackingService.Dto.DocumentResponse;
import com.CourierManagement.TrackingService.Entity.Document;
import com.CourierManagement.TrackingService.Exception.TrackingNotFoundException;
import com.CourierManagement.TrackingService.Repository.DocumentRepository;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final DeliveryClient deliveryClient;  // ← injected via RequiredArgsConstructor

    @Value("${app.upload.dir:uploads/documents}")
    private String uploadDir;

    public DocumentResponse uploadDocument(
            String deliveryId,
            String trackingNumber,
            String documentType,
            String uploadedBy,
            MultipartFile file) throws IOException {

        // ── Feign call: verify delivery exists before uploading document ──
        boolean exists = deliveryClient.doesDeliveryExist(deliveryId);
        if (!exists) {
            throw new TrackingNotFoundException(
                "Cannot upload document — delivery not found with ID: " + deliveryId);
        }

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        String uniqueName   = UUID.randomUUID() + "_" + originalName;
        // .resolve() — joins path + file name safely
        Path   targetPath   = uploadPath.resolve(uniqueName);

        // file is physically stored on your system
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // you are preparing data to store in database
        Document doc = Document.builder()
                .deliveryId(deliveryId)
                .trackingNumber(trackingNumber)
                .fileName(originalName)
                .filePath(targetPath.toString())
                .documentType(documentType)  // e.g. invoice, label, proof of delivery
                .contentType(file.getContentType())  // what kind of file format it is
                .uploadedBy(uploadedBy)
                .build();

        // converts entity -> DTO, sends response back to client
        return toResponse(repository.save(doc));
    }

    public List<DocumentResponse> getDocumentsByDelivery(String deliveryId) {

        // ── Feign call: verify delivery exists before fetching documents ──
        boolean exists = deliveryClient.doesDeliveryExist(deliveryId);
        if (!exists) {
            throw new TrackingNotFoundException(
                "Cannot fetch documents — delivery not found with ID: " + deliveryId);
        }

        List<Document> docs = repository.findByDeliveryIdOrderByUploadedAtDesc(deliveryId);
        if (docs.isEmpty()) {
            throw new TrackingNotFoundException(
                "No documents found for delivery: " + deliveryId);
        }

        // toResponse() converts database entity into API response
        return docs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private DocumentResponse toResponse(Document d) {
        return DocumentResponse.builder()
                .id(d.getId())
                .deliveryId(d.getDeliveryId())
                .trackingNumber(d.getTrackingNumber())
                .fileName(d.getFileName())
                .filePath(d.getFilePath())
                .documentType(d.getDocumentType())
                .contentType(d.getContentType())
                .uploadedBy(d.getUploadedBy())
                .uploadedAt(d.getUploadedAt())
                .build();
    }
}