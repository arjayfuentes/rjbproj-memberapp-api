package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.OrganizationAddress;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record OrganizationRequest(

        UUID organizationId,

        @NotNull(message = "Organization name is required")
        String name,

        @NotNull(message = "Description name is required")
        String description,

        Timestamp createdAt,

        Timestamp updatedAt,

        @NotNull(message = "Address is required")
        OrganizationAddress organizationAddress
) {
}
