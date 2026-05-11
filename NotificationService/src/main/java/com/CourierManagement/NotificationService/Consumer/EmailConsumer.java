package com.CourierManagement.NotificationService.Consumer;

import com.CourierManagement.NotificationService.Dto.EmailNotificationEvent;
import com.CourierManagement.NotificationService.Service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final EmailSenderService emailSenderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "email.queue", durable = "true"),
            exchange = @Exchange(value = "notification.exchange", type = "topic"),
            key = "delivery.status.updated"
    ))
    public void consumeStatusUpdateEvent(EmailNotificationEvent event) {
        log.info("Received EmailNotificationEvent for tracking number: {}", event.getTrackingNumber());
        emailSenderService.sendStatusUpdateEmail(event);
    }
}
