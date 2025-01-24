package com.rjproj.memberapp.member;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MemberRequest (

        UUID memberId,

        @NotNull(message = "Member firstname is required")
        String firstName,

        @NotNull(message = "Member lastname is required")
        String lastName,

        @NotNull(message = "Customer Email is required")
        @Email(message = "Customer Email is not a valid email address")
        String email,

        String password,

        String phoneNumber,

        MemberAddress memberAddress
) {

}
