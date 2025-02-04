package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.OrganizationAddress;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse (
        String organizationId,
        String name,
        String description,
        String logoUrl,
        String backgroundImageUrl,
        String email,
        String phoneNumber,
        String websiteUrl,
        OrganizationAddress organizationAddress,
        Instant createdAt,
        Instant updatedAt
) {
}
