package com.rjproj.memberapp.member.dto;

import com.rjproj.memberapp.member.model.MemberAddress;

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
