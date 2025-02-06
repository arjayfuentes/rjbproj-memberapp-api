package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MembershipType;

import java.util.UUID;

public record CreateMembershipRequest (
        UUID memberId,
        UUID organizationId,
        UUID membershipTypeId
){
}
