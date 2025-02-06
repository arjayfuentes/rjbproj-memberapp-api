package com.rjproj.memberapp.dto;

public record MembershipTypeValidity(
        String membershipTypeValidityId,
        String name,
        Integer duration,
        String description
) {
}
