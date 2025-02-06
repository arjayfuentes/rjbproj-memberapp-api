package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipResponse(
        UUID membershipId,
        UUID organizationId,
        MembershipResponse membershipType,
        String status,
        Timestamp startDate,
        Timestamp endDate
) {
}
