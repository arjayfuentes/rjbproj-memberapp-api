package com.rjproj.memberapp.event.mapper;

import com.rjproj.memberapp.event.dto.EventRequest;
import com.rjproj.memberapp.event.dto.EventResponse;
import com.rjproj.memberapp.event.model.Event;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {

    public Event toEvent(EventRequest eventRequest) {
        return Event.builder()
                .organizationId(eventRequest.organizationId())
                .name(eventRequest.name())
                .description(eventRequest.description())
                .startEventDate(eventRequest.startEventDate())
                .endEventDate(eventRequest.endEventDate())
                .eventAddress(eventRequest.eventAddress())
                .build();
    }


    public EventResponse fromEvent(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getOrganizationId(),
                event.getName(),
                event.getDescription(),
                event.getStartEventDate(),
                event.getEndEventDate(),
                event.getEventAddress(),
                event.getCreatedAt(),
                event.getUpdatedAt()

        );
    }
}
