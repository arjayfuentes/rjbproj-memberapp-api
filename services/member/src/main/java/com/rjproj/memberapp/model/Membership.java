package com.rjproj.memberapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID membershipId;

    UUID organizationId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "membership_type_id", nullable = false)
    MembershipType membershipType;

    String status;

    Timestamp startDate;

    Timestamp endDate;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
