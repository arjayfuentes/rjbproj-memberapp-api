package com.rjproj.memberapp.organization;

import java.time.Instant;

public record OrganizationResponse(String organizationId, String name, String description, OrganizationAddress organizationAddress) {
    public OrganizationResponse(String organizationId, String name, String description, OrganizationAddress organizationAddress) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.organizationAddress = organizationAddress;
    }

    public String organizationId() {
        return this.organizationId;
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return this.description;
    }

    public OrganizationAddress organizationAddress() {
        return this.organizationAddress;
    }
}
