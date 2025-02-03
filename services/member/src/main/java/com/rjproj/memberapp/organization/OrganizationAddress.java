package com.rjproj.memberapp.organization;

import java.time.Instant;

public record OrganizationAddress(String organizationAddressId, String street, String city, String provinceState, String region, String country) {
    public OrganizationAddress(String organizationAddressId, String street, String city, String provinceState, String region, String country) {
        this.organizationAddressId = organizationAddressId;
        this.street = street;
        this.city = city;
        this.provinceState = provinceState;
        this.region = region;
        this.country = country;
    }

    public String organizationAddressId() {
        return this.organizationAddressId;
    }

    public String street() {
        return this.street;
    }

    public String city() {
        return this.city;
    }

    public String provinceState() {
        return this.provinceState;
    }

    public String region() {
        return this.region;
    }

    public String country() {
        return this.country;
    }
}
