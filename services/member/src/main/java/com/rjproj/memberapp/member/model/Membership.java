package com.rjproj.memberapp.member.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
public class Membership {

    @Id
    private UUID membershipId;

    private UUID memberId;

    private UUID organizationId;

    private UUID membershipTypeId;

    private String status;

    private Timestamp startDate ;

    private Timestamp endDate ;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
