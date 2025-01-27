package com.rjproj.memberapp.kafka.event;

import java.time.Instant;

public record OrganizationAddress(
        String organizationAddressId,
        String street,
        String city,
        String provinceState,
        String region,
        String country,
        Instant createdAt,
        Instant updatedAt
) {
}
