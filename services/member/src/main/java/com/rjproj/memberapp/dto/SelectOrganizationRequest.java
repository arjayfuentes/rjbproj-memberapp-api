package com.rjproj.memberapp.dto;

import java.util.UUID;

public record SelectOrganizationRequest(
        UUID organizationId,
        UUID memberId
) {
}
