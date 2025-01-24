package com.rjproj.memberapp.address;

import com.rjproj.memberapp.organization.Organization;
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
public class OrganizationAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID organizationAddressId;

    private String street;

    private String city;

    private String provinceState;

    private String region;

    private String country;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne(mappedBy = "organizationAddress")
    private Organization organization; // Bidirectional mapping
}