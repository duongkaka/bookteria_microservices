package com.devteria.notification.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.devteria.event.dto.NotificationEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationController {

    @KafkaListener(topics = "notification-delivery", groupId = "notification-group")
    public void listenNotificationDelivery2(NotificationEvent message) {
        log.info("Message received: {}", message);
    }

    @KafkaListener(topics = "notification-delivery", groupId = "notification-group")
    public void listenNotificationDelivery(NotificationEvent message) {
        log.info("Message received: {}", message);
        //        gmailService.sendEmail(SendEmailRequest.builder()
        //                .to(message.getRecipient())
        //                .subject(message.getSubject())
        //                .htmlContent(message.getBody())
        //                .build());
    }
}
