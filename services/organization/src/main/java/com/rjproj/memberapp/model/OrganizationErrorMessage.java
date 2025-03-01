package com.rjproj.memberapp.model;

public enum OrganizationErrorMessage {
    UNAUTHORIZED("Unauthorized"),
    ACCESS_DENIED("Access denied"),
    ORGANIZATION_EXISTS("Organization already exists"),
    ORGANIZATION_EXISTS_NOT_EXISTS ("Organization does not exists");

    private final String message;

    OrganizationErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}