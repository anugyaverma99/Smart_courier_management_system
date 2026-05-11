package com.CourierManagement.NotificationService.Service;

import com.CourierManagement.NotificationService.Dto.EmailNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendStatusUpdateEmail(EmailNotificationEvent event) {
        if (event.getCustomerEmail() == null || event.getCustomerEmail().isEmpty()) {
            log.warn("Cannot send email: Customer email is empty for tracking number {}", event.getTrackingNumber());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(event.getCustomerEmail());
            helper.setSubject("Parcel Status Update: " + event.getTrackingNumber());

            String htmlContent = buildHtmlContent(event);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to {} for tracking number {}", event.getCustomerEmail(), event.getTrackingNumber());

        } catch (MessagingException e) {
            log.error("Failed to send email to {}", event.getCustomerEmail(), e);
        }
    }

    private String buildHtmlContent(EmailNotificationEvent event) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>SmartCourier Delivery Update</h2>" +
                "<p>Hello <strong>" + (event.getCustomerName() != null ? event.getCustomerName() : "Customer") + "</strong>,</p>" +
                "<p>There is an update regarding your parcel with tracking number: <strong>" + event.getTrackingNumber() + "</strong></p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #007bff; margin: 20px 0;'>" +
                "<p style='margin: 0; font-size: 16px;'>New Status: <strong style='color: #007bff;'>" + event.getStatus() + "</strong></p>" +
                (event.getRemarks() != null ? "<p style='margin: 5px 0 0 0;'>Remarks: " + event.getRemarks() + "</p>" : "") +
                "</div>" +
                "<p>You can track your delivery in real-time by logging into your dashboard.</p>" +
                "<p>Best regards,<br/>The SmartCourier Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
