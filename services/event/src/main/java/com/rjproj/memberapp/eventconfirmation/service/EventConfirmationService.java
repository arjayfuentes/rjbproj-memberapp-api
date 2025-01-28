package com.rjproj.memberapp.eventconfirmation.service;

import com.rjproj.memberapp.eventconfirmation.dto.EventConfirmationRequest;
import com.rjproj.memberapp.eventconfirmation.dto.EventConfirmationResponse;
import com.rjproj.memberapp.eventconfirmation.mapper.EventConfirmationMapper;
import com.rjproj.memberapp.eventconfirmation.model.EventConfirmation;
import com.rjproj.memberapp.eventconfirmation.repository.EventConfirmationRepository;
import com.rjproj.memberapp.exception.BusinessException;
import com.rjproj.memberapp.kafka.EventProducer;
import com.rjproj.memberapp.member.MemberClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventConfirmationService {

    private final EventConfirmationRepository eventConfirmationRepository;

    private final EventConfirmationMapper eventConfirmationMapper;

    private final MemberClient memberClient;

    private final EventProducer eventProducer;

    public EventConfirmationResponse createEventConfirmation(@Valid EventConfirmationRequest eventRequest) {
        EventConfirmation eventConfirmation = eventConfirmationMapper.toEventConfirmation(eventRequest);
        eventProducer.sendEventConfirmation(eventConfirmation);

        var member = this.memberClient.getMember(eventRequest.memberId());

        return eventConfirmationMapper.fromEventConfirmation(eventConfirmationRepository.save(eventConfirmation));
    }

    public List<EventConfirmationResponse> findAll() {
        return eventConfirmationRepository.findAll()
                .stream()
                .map(eventConfirmationMapper::fromEventConfirmation)
                .collect(Collectors.toList());
    }

    public EventConfirmationResponse findById(UUID eventConfirmationId) {
        return eventConfirmationRepository.findById(eventConfirmationId)
                .map(eventConfirmationMapper::fromEventConfirmation)
                .orElseThrow(() -> new EntityNotFoundException("EventConfirmation not found with ID:: " + eventConfirmationId));
    }

    public EventConfirmationResponse updateEventConfirmation(@Valid EventConfirmationRequest eventRequest) {
        EventConfirmation eventConfirmation = eventConfirmationRepository.findById(eventRequest.eventConfirmationId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update eventConfirmation with id %s", eventRequest.eventConfirmationId())
                ));
        mergeEventConfirmation(eventConfirmation, eventRequest);
        return eventConfirmationMapper.fromEventConfirmation(eventConfirmationRepository.save(eventConfirmation));
    }


    private void mergeEventConfirmation(EventConfirmation eventConfirmation, @Valid EventConfirmationRequest eventConfirmationRequest) {
        if(eventConfirmationRequest.event() != null) {
            eventConfirmation.setEvent(eventConfirmationRequest.event());
        }
        if(eventConfirmationRequest.memberId() != null) {
            eventConfirmation.setMemberId(eventConfirmationRequest.memberId());
        }
        if(eventConfirmationRequest.confirmationStatus() != null) {
            eventConfirmation.setConfirmationStatus(eventConfirmationRequest.confirmationStatus());
        }
        if(eventConfirmationRequest.confirmationDate() != null) {
            eventConfirmation.setConfirmationDate(eventConfirmationRequest.confirmationDate());
        }
    }
}