package com.rjproj.memberapp.organization;

import com.rjproj.memberapp.address.OrganizationAddress;
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
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID organizationId;

    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "organization_address_id", nullable = false)
    private OrganizationAddress organizationAddress;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
