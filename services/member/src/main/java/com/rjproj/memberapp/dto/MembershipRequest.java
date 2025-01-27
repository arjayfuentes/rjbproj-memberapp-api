package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MembershipType;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipRequest (

        UUID membershipId,

        @NotNull(message = "Organization is required")
        UUID organizationId,

        @NotNull(message = "Member is required")
//        UUID memberId,
        Member member,

        @NotNull(message = "Member is required")
//        UUID membershipTypeId,
        MembershipType membershipType,

        String status,

        @NotNull(message = "Start Date is required")
        Timestamp startDate,

        @NotNull(message = "End Date is required")
        Timestamp endDate
) {
}
