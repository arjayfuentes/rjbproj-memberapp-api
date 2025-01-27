package com.rjproj.memberapp.event.dto;

import com.rjproj.memberapp.event.model.EventAddress;

import java.sql.Timestamp;
import java.util.UUID;

public record EventResponse(
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
