package com.rjproj.memberapp.member;

import java.time.Instant;

public record MemberAddress(
        String memberAddressId,
        String street,
        String city,
        String provinceState,
        String region,
        String country,
        Instant createdAt,
        Instant updatedAt
) {
}
