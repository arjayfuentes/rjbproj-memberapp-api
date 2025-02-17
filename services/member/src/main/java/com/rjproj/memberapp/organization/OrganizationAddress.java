package com.rjproj.memberapp.organization;

import java.time.Instant;

public record OrganizationAddress(
        String organizationAddressId,
        String street,
        String city,
        String provinceState,
        String region,
        String postalCode,
        String country
) {
}
