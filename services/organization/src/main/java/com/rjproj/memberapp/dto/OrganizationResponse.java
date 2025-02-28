package com.rjproj.memberapp.dto;

public record OrganizationResponse (
        String organizationId,
        String name,
        String description,
        String logoUrl,
        String backgroundImageUrl,
        String email,
        String phoneNumber,
        String websiteUrl,
        OrganizationAddressResponse organizationAddress
) {
}
