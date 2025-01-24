package com.rjproj.memberapp.member;

import java.util.UUID;

public record MemberResponse (
        UUID memberId,
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        MemberAddress memberAddress
) {
}
