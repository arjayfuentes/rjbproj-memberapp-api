package com.rjproj.memberapp.kafka.event;

import java.time.Instant;

public record Organization(
        String organizationId,
        String name,
        String description,
        OrganizationAddress organizationAddress,
        Instant createdAt,
        Instant updatedAt
) {
}
