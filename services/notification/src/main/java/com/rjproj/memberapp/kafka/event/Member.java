package com.rjproj.memberapp.kafka.event;

import java.sql.Timestamp;
import java.util.UUID;

public record Member(
        UUID memberId,
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        MemberAddress memberAddress,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
