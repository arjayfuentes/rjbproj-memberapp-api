package com.rjproj.memberapp.eventconfirmation.dto;

import com.rjproj.memberapp.event.model.Event;
import com.rjproj.memberapp.eventconfirmation.model.ConfirmationStatus;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record EventConfirmationRequest (

        UUID eventConfirmationId,

        @NotNull(message = "Event is required")
        Event event,

        @NotNull(message = "Member is required")
        UUID memberId,

        @NotNull(message = "Confirmation Status is required")
        ConfirmationStatus confirmationStatus,

        Timestamp confirmationDate,

        Timestamp createdAt,

        Timestamp updatedAt
) {
}
