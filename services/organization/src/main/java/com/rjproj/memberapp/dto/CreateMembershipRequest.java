package com.rjproj.memberapp.dto;

import java.util.UUID;

public record CreateMembershipRequest(
        UUID memberId,
        String organizationId,
        UUID membershipTypeId
) {
}
