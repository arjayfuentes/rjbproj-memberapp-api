package com.rjproj.memberapp.event.service;

import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.event.dto.EventRequest;
import com.rjproj.memberapp.event.dto.EventResponse;
import com.rjproj.memberapp.event.mapper.EventMapper;
import com.rjproj.memberapp.event.model.Event;
import com.rjproj.memberapp.event.repository.EventRepository;
import com.rjproj.memberapp.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final OrganizationClient organizationClient;

    public EventResponse createEvent(@Valid EventRequest eventRequest) {
        var organization = this.organizationClient.findOrganizationById(eventRequest.organizationId())
                .orElseThrow(() -> new BusinessException("Organization not found"));

        Event event = eventMapper.toEvent(eventRequest);
        return eventMapper.fromEvent(eventRepository.save(event));
    }

    public List<EventResponse> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::fromEvent)
                .collect(Collectors.toList());
    }

    public EventResponse findById(UUID eventId) {
        return eventRepository.findById(eventId)
                .map(eventMapper::fromEvent)
                .orElseThrow(() -> new BusinessException("Event not found with ID:: " + eventId));
    }

    public EventResponse updateEvent(@Valid EventRequest eventRequest) {
        Event event = eventRepository.findById(eventRequest.eventId())
                .orElseThrow(() -> new BusinessException(
                        String.format("Cannot update event with id %s", eventRequest.eventId())
                ));
        mergeEvent(event, eventRequest);
        return eventMapper.fromEvent(eventRepository.save(event));
    }


    private void mergeEvent(Event event, @Valid EventRequest eventRequest) {
        if(eventRequest.organizationId() != null) {
            event.setOrganizationId(eventRequest.organizationId());
        }
        if(StringUtils.isNotBlank(eventRequest.name())) {
            event.setName(eventRequest.name());
        }
        if(StringUtils.isNotBlank(eventRequest.description())) {
            event.setDescription(eventRequest.description());
        }
        if(eventRequest.startEventDate() != null) {
            event.setStartEventDate(eventRequest.startEventDate());
        }
        if(eventRequest.endEventDate() != null) {
            event.setEndEventDate(eventRequest.endEventDate());
        }
        if(eventRequest.eventAddress() != null) {
            event.setEventAddress(eventRequest.eventAddress());
        }
    }
}
