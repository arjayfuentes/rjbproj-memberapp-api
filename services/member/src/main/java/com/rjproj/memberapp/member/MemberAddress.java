package com.rjproj.memberapp.member;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Document
@Validated
public class MemberAddress {

    private String street;

    private String city;

    private String provinceState;

    private String region;

    private String country;

    private Timestamp createdAt;

    private Timestamp updatedAt;


}
