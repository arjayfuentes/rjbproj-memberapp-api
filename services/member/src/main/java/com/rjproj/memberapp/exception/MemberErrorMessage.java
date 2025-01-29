package com.rjproj.memberapp.exception;

public enum MemberErrorMessage {
    UNAUTHORIZED("Unauthorized"),
    ACCESS_DENIED("Access denied"),
    MEMBER_EXISTS("Member already exists");

    private final String message;

    MemberErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
