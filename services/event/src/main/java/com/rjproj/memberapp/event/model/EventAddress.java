package com.rjproj.memberapp.event.model;

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
public class EventAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID eventAddressId;

    private String street;

    private String city;

    private String provinceState;

    private String region;

    private String country;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "eventAddress")
    private Event event;

}