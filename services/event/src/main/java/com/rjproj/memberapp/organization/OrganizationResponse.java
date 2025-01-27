package com.rjproj.memberapp.organization;

import java.time.Instant;

public record OrganizationResponse (
        String organizationId,
        String name,
        String description,
        OrganizationAddress organizationAddress,
        Instant createdAt,
        Instant updatedAt
) {
}
