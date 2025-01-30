package com.rjproj.memberapp.organization;

import java.time.Instant;

public record OrganizationResponse(String organizationId, String name, String description, OrganizationAddress organizationAddress, Instant createdAt, Instant updatedAt) {
    public OrganizationResponse(String organizationId, String name, String description, OrganizationAddress organizationAddress, Instant createdAt, Instant updatedAt) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.organizationAddress = organizationAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Instant createdAt() {
        return this.createdAt;
    }

    public Instant updatedAt() {
        return this.updatedAt;
    }
}
