package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.OrganizationAddress;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse (
        String organizationId,
        String name,
        String description,
        OrganizationAddress organizationAddress,
        Instant createdAt,
        Instant updatedAt
) {
}
