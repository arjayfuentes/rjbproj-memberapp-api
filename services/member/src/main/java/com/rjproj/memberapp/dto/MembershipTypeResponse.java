package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipTypeResponse (
        UUID membershipTypeId,
        UUID organizationId,
        String name,
        String description,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
