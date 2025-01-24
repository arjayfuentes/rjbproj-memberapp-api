package com.rjproj.memberapp.member;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Document
public class Member {

    @Id
    private UUID memberId;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private MemberAddress memberAddress;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
