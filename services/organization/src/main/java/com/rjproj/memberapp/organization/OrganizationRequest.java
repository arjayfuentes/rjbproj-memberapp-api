package com.rjproj.memberapp.organization;

import com.rjproj.memberapp.address.OrganizationAddress;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
