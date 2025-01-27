package com.rjproj.memberapp.dto;

import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipTypeRequest(
        UUID membershipTypeId,

        @NotNull(message = "Organization is required")
        UUID organizationId,

        @NotNull(message = "Name is required")
        String name,

        String description
) {
}
