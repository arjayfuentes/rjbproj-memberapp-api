package com.rjproj.memberapp.dto;

import java.util.UUID;

public record GetMyOrganizationRequest (
        UUID memberId
) {
}
