package com.rjproj.memberapp.kafka.event;

import java.sql.Timestamp;
import java.util.UUID;

public record EventAddress(
        UUID eventAddressId,
        String street,
        String city,
        String provinceState,
        String region,
        String country,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
