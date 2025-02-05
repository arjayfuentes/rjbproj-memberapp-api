package com.rjproj.memberapp.dto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetMembershipRequest(

        @NotNull(message = "Member is required")
        UUID memberId,

        @NotNull(message = "Organization is required")
        UUID organizationId
) {
}
