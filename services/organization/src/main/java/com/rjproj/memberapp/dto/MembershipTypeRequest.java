package com.rjproj.memberapp.dto;

public record MembershipTypeRequest(
    String membershipTypeId,
    String organizationId,
    MembershipTypeValidity membershipTypeValidity,
    String name,
    String description,
    Boolean isDefault
) {
}
