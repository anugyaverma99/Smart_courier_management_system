package com.CourierManagement.TrackingService.Service;

import com.CourierManagement.TrackingService.Client.DeliveryClient;
import com.CourierManagement.TrackingService.Dto.DocumentResponse;
import com.CourierManagement.TrackingService.Entity.Document;
import com.CourierManagement.TrackingService.Repository.DocumentRepository;
import com.CourierManagement.TrackingService.Exception.TrackingNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @InjectMocks
    private DocumentService service;

    @Mock
    private DocumentRepository repository;

    @Mock
    private DeliveryClient deliveryClient;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Set private field uploadDir via reflection
        Field uploadDirField = DocumentService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(service, "test-uploads");
    }

    @Test
    void testUploadDocument_success() throws IOException {
        String deliveryId = "1";
        String trackingNumber = "TRK-1234";
        String docType = "invoice";
        String uploadedBy = "tester";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        when(deliveryClient.doesDeliveryExist(deliveryId)).thenReturn(true);
        when(repository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DocumentResponse response = service.uploadDocument(
                deliveryId, trackingNumber, docType, uploadedBy, file
        );

        assertNotNull(response);
        assertEquals(deliveryId, response.getDeliveryId());
        assertEquals(trackingNumber, response.getTrackingNumber());
        assertEquals("test.pdf", response.getFileName());
        assertEquals(docType, response.getDocumentType());
        assertEquals(uploadedBy, response.getUploadedBy());

        // Clean up uploaded file
        Path path = Paths.get(response.getFilePath());
        Files.deleteIfExists(path);
    }

    @Test
    void testUploadDocument_deliveryNotFound() {
        String deliveryId = "999";
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());

        when(deliveryClient.doesDeliveryExist(deliveryId)).thenReturn(false);

        assertThrows(TrackingNotFoundException.class, () -> {
            service.uploadDocument(deliveryId, "TRK-999", "invoice", "tester", file);
        });
    }

    @Test
    void testGetDocumentsByDelivery_success() {
        String deliveryId = "1";

        when(deliveryClient.doesDeliveryExist(deliveryId)).thenReturn(true);

        Document doc = Document.builder()
                .id(1L)
                .deliveryId(deliveryId)
                .trackingNumber("TRK-1234")
                .fileName("file.pdf")
                .filePath("test-uploads/file.pdf")
                .documentType("invoice")
                .uploadedBy("tester")
                .build();

        when(repository.findByDeliveryIdOrderByUploadedAtDesc(deliveryId))
                .thenReturn(List.of(doc));

        List<DocumentResponse> docs = service.getDocumentsByDelivery(deliveryId);

        assertEquals(1, docs.size());
        assertEquals("file.pdf", docs.get(0).getFileName());
    }

    @Test
    void testGetDocumentsByDelivery_deliveryNotFound() {
        String deliveryId = "999";
        when(deliveryClient.doesDeliveryExist(deliveryId)).thenReturn(false);

        assertThrows(TrackingNotFoundException.class,
                () -> service.getDocumentsByDelivery(deliveryId));
    }

    @Test
    void testGetDocumentsByDelivery_noDocuments() {
        String deliveryId = "1";
        when(deliveryClient.doesDeliveryExist(deliveryId)).thenReturn(true);
        when(repository.findByDeliveryIdOrderByUploadedAtDesc(deliveryId)).thenReturn(List.of());

        assertThrows(TrackingNotFoundException.class,
                () -> service.getDocumentsByDelivery(deliveryId));
    }
}