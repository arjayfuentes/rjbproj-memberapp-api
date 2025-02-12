package com.rjproj.memberapp.dto;

public record OrganizationAddressResponse(
        String organizationAddressId,
        String street,
        String city,
        String provinceState,
        String region,
        String postCode,
        String country
) {
}
