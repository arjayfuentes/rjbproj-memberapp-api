package com.rjproj.memberapp.dto;

import java.util.List;

public record CreateOrganizationRequest(
        OrganizationRequest organizationRequest,
        List<MembershipTypeRequest> membershipTypes
) {
}
