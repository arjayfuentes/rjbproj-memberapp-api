package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.OrganizationAddress;
import jakarta.validation.constraints.NotNull;

public record OrganizationRequest(

        String organizationId,

        @NotNull(message = "Organization name is required")
        String name,

        @NotNull(message = "Description name is required")
        String description,

        String logoUrl,

        String backgroundImageUrl,

        @NotNull(message = "Email is required")
        String email,

        String phoneNumber,

        String websiteUrl,

        OrganizationAddress organizationAddress

) {
}
