package com.rjproj.memberapp.kafka.event;

import java.sql.Timestamp;
import java.util.UUID;

public record MemberAddress(
        UUID memberAddressId,
        String street,
        String city,
        String provinceState,
        String region,
        String country,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
