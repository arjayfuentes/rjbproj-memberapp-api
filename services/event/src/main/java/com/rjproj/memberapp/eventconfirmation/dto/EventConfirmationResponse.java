package com.rjproj.memberapp.eventconfirmation.dto;

import com.rjproj.memberapp.event.model.Event;
import com.rjproj.memberapp.eventconfirmation.model.ConfirmationStatus;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record EventConfirmationResponse (
        UUID eventConfirmationId,
        Event event,
        UUID memberId,
        ConfirmationStatus confirmationStatus,
        Timestamp confirmationDate,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
