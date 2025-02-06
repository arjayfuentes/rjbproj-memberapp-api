package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipTypeResponse (
        UUID membershipTypeId,
        UUID organizationId,
        MembershipTypeValidity membershipTypeValidity,
        String name,
        String description,
        Boolean isDefault,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
