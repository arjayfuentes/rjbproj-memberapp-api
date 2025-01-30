package com.rjproj.memberapp.organization;

import java.time.Instant;

public record OrganizationAddress(String organizationAddressId, String street, String city, String provinceState, String region, String country, Instant createdAt, Instant updatedAt) {
    public OrganizationAddress(String organizationAddressId, String street, String city, String provinceState, String region, String country, Instant createdAt, Instant updatedAt) {
        this.organizationAddressId = organizationAddressId;
        this.street = street;
        this.city = city;
        this.provinceState = provinceState;
        this.region = region;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Instant createdAt() {
        return this.createdAt;
    }

    public Instant updatedAt() {
        return this.updatedAt;
    }
}
