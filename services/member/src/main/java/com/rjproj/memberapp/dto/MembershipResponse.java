package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MembershipStatus;
import com.rjproj.memberapp.model.MembershipType;
import com.rjproj.memberapp.model.Role;

import java.sql.Timestamp;
import java.util.UUID;

public record MembershipResponse(
        UUID membershipId,
        UUID organizationId,
        MemberResponse member,
        MembershipType membershipType,
        MembershipStatusResponse membershipStatus,
        RoleResponse role,
        Timestamp startDate,
        Timestamp endDate
) {
}
