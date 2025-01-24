package com.rjproj.memberapp.organization;

import com.rjproj.memberapp.address.OrganizationAddress;

import java.sql.Timestamp;
import java.util.UUID;

public record OrganizationResponse (
        UUID organizationId,
        String name,
        String description,
        Timestamp createdAt,
        Timestamp updatedAt,
        OrganizationAddress organizationAddress
) {
}
