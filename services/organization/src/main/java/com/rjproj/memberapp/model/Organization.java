package com.rjproj.memberapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Organization {

    @Id
    private String organizationId;

    private String name;

    private String description;

    private String logoUrl;

    private String backgroundImageUrl;

    private String email;

    private String phoneNumber;

    private String websiteUrl;

    private OrganizationAddress organizationAddress;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
