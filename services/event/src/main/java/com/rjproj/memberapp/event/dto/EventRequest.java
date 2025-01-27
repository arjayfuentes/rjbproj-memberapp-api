package com.rjproj.memberapp.event.dto;

import com.rjproj.memberapp.event.model.EventAddress;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

public record EventRequest(

        UUID eventId,

        UUID organizationId,

        @NotNull(message = "Organization name is required")
        String name,

        @NotNull(message = "Description name is required")
        String description,

        @NotNull(message = "start event date is required")
        Timestamp startEventDate,

        @NotNull(message = "end event date is required")
        Timestamp endEventDate,

        @NotNull(message = "Event address is required")
        EventAddress eventAddress
) {
}
