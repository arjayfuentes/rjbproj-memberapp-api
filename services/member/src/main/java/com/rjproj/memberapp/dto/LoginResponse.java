package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.organization.OrganizationResponse;

import java.util.List;
import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        MemberResponse member,
        List<String> permissions,
        UUID selectedOrganizationId,
        List<UUID> organizationIdsOfMember
) {
}
