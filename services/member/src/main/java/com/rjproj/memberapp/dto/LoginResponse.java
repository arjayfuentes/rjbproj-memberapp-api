package com.rjproj.memberapp.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        MemberResponse memberResponse
) {
}
