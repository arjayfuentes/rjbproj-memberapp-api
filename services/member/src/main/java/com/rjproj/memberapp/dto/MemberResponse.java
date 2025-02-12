package com.rjproj.memberapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rjproj.memberapp.model.MemberAddress;
import com.rjproj.memberapp.model.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record MemberResponse (
        UUID memberId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String profilePicUrl,

        LocalDate birthDate,
        LoginType loginType,
        MemberAddressResponse memberAddress,
        Timestamp createdAt
) {
}
