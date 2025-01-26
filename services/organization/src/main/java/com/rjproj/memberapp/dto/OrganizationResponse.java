package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.OrganizationAddress;

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
