package com.rjproj.memberapp.kafka;


import com.rjproj.memberapp.eventconfirmation.model.EventConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaTemplate<String, EventConfirmation> kafkaTemplate;

    public void sendEventConfirmation(EventConfirmation eventConfirmation) {
        log.info("Sending event confirmation");
        Message<EventConfirmation> message = MessageBuilder
                .withPayload(eventConfirmation)
                .setHeader(TOPIC, "event-topic")
                .build();

        kafkaTemplate.send(message);
    }

}
