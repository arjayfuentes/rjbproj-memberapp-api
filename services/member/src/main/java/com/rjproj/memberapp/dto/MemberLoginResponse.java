package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MemberAddress;

import java.sql.Timestamp;
import java.util.UUID;

public record MemberLoginResponse  (
        UUID memberId,
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        MemberAddress memberAddress,
        Timestamp createdAt,
        Timestamp updatedAt
)  {
}
