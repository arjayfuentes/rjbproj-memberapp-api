package com.rjproj.memberapp.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Validated
public class OrganizationAddress {

    @Id
    private String organizationAddressId;

    private String street;

    private String city;

    private String provinceState;

    private String region;

    private String postalCode;

    private String country;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
