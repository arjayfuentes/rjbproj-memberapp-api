package com.rjproj.memberapp.dto;

import java.util.UUID;

public record CreateOwnerRoleRequest(
        UUID organizationId
) {
}
