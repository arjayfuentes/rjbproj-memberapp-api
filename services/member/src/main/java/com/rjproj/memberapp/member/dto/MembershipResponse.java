package com.rjproj.memberapp.member.dto;

import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipResponse(

        UUID membershipId,
        UUID memberId,
        UUID organizationId,
        UUID membershipTypeId,
        String status,
        Timestamp startDate,
        Timestamp endDate
) {
}
