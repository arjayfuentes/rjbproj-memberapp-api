package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MembershipType;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipResponse(
        UUID membershipId,
        UUID organizationId,
        MembershipType membershipType,
        String status,
        Timestamp startDate,
        Timestamp endDate,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}
