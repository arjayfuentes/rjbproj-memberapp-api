package com.rjproj.memberapp.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        MemberResponse member,
        List<String> permissions
) {
}
