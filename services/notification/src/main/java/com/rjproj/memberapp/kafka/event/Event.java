package com.rjproj.memberapp.kafka.event;

import java.sql.Timestamp;
import java.util.UUID;

public record Event(
        UUID eventId,
        UUID organizationId,
        String name,
        String description,
        Timestamp startEventDate,
        Timestamp endEventDate,
        EventAddress eventAddress,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
