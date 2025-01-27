package com.rjproj.memberapp.eventconfirmation.repository;

import com.rjproj.memberapp.eventconfirmation.model.EventConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventConfirmationRepository extends JpaRepository<EventConfirmation, UUID> {
}
