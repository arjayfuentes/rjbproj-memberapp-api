package com.rjproj.memberapp.eventconfirmation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rjproj.memberapp.event.model.Event;
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
public class EventConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID eventConfirmationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfirmationStatus confirmationStatus;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp confirmationDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;
}

