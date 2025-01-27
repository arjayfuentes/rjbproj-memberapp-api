package com.rjproj.memberapp.kafka.event;

import java.sql.Timestamp;
import java.util.UUID;

public record EventConfirmation(
        UUID eventConfirmationId,
        Event event,
        UUID memberId,
        ConfirmationStatus confirmationStatus,
        Timestamp confirmationDate,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
