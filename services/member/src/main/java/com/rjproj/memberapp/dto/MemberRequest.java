package com.rjproj.memberapp.dto;

import java.util.UUID;

import com.rjproj.memberapp.model.MemberAddress;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MemberRequest (

        UUID memberId,

        @NotNull(message = "Member firstname is required")
        String firstName,

        @NotNull(message = "Member lastname is required")
        String lastName,

        @NotNull(message = "Member Email is required")
        @Email(message = "Member Email is not a valid email address")
        String email,

        String password,

        String phoneNumber,

        String profilePicUrl,

        MemberAddress memberAddress
) {

}
