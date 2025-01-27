package com.rjproj.memberapp.kafka;

import com.rjproj.memberapp.kafka.event.EventConfirmation;
import com.rjproj.memberapp.model.Notification;
import com.rjproj.memberapp.model.NotificationType;
import com.rjproj.memberapp.repository.NotificationRepository;
import com.rjproj.memberapp.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @KafkaListener(topics = "event-topic")
    public void consumeEventConfirmationSuccessNotifications(EventConfirmation eventConfirmation) throws MessagingException {
        log.info(format("Consuming the message from payment-topic Topic:: %s", eventConfirmation));
        repository.save(
                Notification.builder()
                        .type(NotificationType.YES)
                        .notificationDate(LocalDateTime.now())
                        .eventConfirmation(eventConfirmation)
                        .build()
        );
        var customerName = eventConfirmation.memberId() + " " + eventConfirmation.confirmationStatus();
        emailService.sendConfirmationSuccessfulEmail(
                "arjay60@gmail.com",
                eventConfirmation.memberId(),
                eventConfirmation.confirmationStatus().toString()
        );
    }

//    @KafkaListener(topics = "order-topic")
//    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) throws MessagingException {
//        log.info(format("Consuming the message from order-topic Topic:: %s", orderConfirmation));
//        repository.save(
//                Notification.builder()
//                        .type(ORDER_CONFIRMATION)
//                        .notificationDate(LocalDateTime.now())
//                        .orderConfirmation(orderConfirmation)
//                        .build()
//        );
//        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
//        emailService.sendOrderConfirmationEmail(
//                orderConfirmation.customer().email(),
//                customerName,
//                orderConfirmation.totalAmount(),
//                orderConfirmation.orderReference(),
//                orderConfirmation.products()
//        );
//    }
}