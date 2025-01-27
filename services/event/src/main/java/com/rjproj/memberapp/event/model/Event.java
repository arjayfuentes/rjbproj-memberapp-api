package com.rjproj.memberapp.event.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rjproj.memberapp.eventconfirmation.model.EventConfirmation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID eventId;

    private UUID organizationId;

    private String name;

    private String description;

    @CreationTimestamp
    private Timestamp startEventDate;

    @UpdateTimestamp
    private Timestamp endEventDate;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventConfirmation> eventConfirmations;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "event_address_id", referencedColumnName = "eventAddressId", nullable = true, unique = true)
    private EventAddress eventAddress;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
