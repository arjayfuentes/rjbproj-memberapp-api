package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.MemberAddress;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

public record MemberResponse (
        UUID memberId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String profilePicUrl,
        MemberAddressResponse memberAddress,
        Timestamp createdAt
) {
}
