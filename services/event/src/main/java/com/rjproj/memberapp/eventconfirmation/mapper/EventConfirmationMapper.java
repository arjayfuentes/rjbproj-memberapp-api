package com.rjproj.memberapp.eventconfirmation.mapper;


import com.rjproj.memberapp.eventconfirmation.dto.EventConfirmationRequest;
import com.rjproj.memberapp.eventconfirmation.dto.EventConfirmationResponse;
import com.rjproj.memberapp.eventconfirmation.model.EventConfirmation;
import org.springframework.stereotype.Service;

@Service
public class EventConfirmationMapper {

    public EventConfirmation toEventConfirmation(EventConfirmationRequest eventConfirmationRequest) {
        return EventConfirmation.builder()
                .eventConfirmationId(eventConfirmationRequest.eventConfirmationId())
                .event(eventConfirmationRequest.event())
                .memberId(eventConfirmationRequest.memberId())
                .confirmationStatus(eventConfirmationRequest.confirmationStatus())
                .confirmationDate(eventConfirmationRequest.confirmationDate())
                .build();
    }


    public EventConfirmationResponse fromEventConfirmation(EventConfirmation eventConfirmation) {
        return new EventConfirmationResponse(
                eventConfirmation.getEventConfirmationId(),
                eventConfirmation.getEvent(),
                eventConfirmation.getMemberId(),
                eventConfirmation.getConfirmationStatus(),
                eventConfirmation.getConfirmationDate(),
                eventConfirmation.getCreatedAt(),
                eventConfirmation.getUpdatedAt()
        );
    }
}
