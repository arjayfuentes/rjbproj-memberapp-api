package com.rjproj.memberapp.member.dto;

import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipRequest (
        
        UUID membershipId,

        @NotNull(message = "Membership Id is required")
        UUID memberId,

        @NotNull(message = "Organization Id is required")
        UUID organizationId,

        @NotNull(message = "MembershipType Id is required")
        UUID membershipTypeId,

        @NotNull(message = "Status is required")
        String status,

        @NotNull(message = "Membership start Date is required")
        Timestamp startDate,

        @NotNull(message = "Membership end Date is required")
        Timestamp endDate
) {
}
