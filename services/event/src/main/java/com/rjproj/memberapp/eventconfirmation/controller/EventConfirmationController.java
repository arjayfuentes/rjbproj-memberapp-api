package com.rjproj.memberapp.eventconfirmation.controller;

import com.rjproj.memberapp.eventconfirmation.dto.EventConfirmationRequest;
import com.rjproj.memberapp.eventconfirmation.dto.EventConfirmationResponse;
import com.rjproj.memberapp.eventconfirmation.service.EventConfirmationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event-confirmation")
@RequiredArgsConstructor
public class EventConfirmationController {


    private final EventConfirmationService eventConfirmationService;

    @PostMapping
    public ResponseEntity<EventConfirmationResponse> createEventConfirmation(@RequestBody @Valid EventConfirmationRequest evenRequest) {
        return ResponseEntity.ok(eventConfirmationService.createEventConfirmation(evenRequest));
    }

    @PutMapping
    public ResponseEntity<EventConfirmationResponse> updateOrganization(@RequestBody @Valid EventConfirmationRequest evenRequest){
        return new ResponseEntity<>(
                eventConfirmationService.updateEventConfirmation(evenRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/{event-confirmation-id}")
    public ResponseEntity<EventConfirmationResponse> findById(
            @PathVariable("event-confirmation-id") UUID eventConfirmationId
    ) {
        return ResponseEntity.ok(eventConfirmationService.findById(eventConfirmationId));
    }

    @GetMapping
    public ResponseEntity<List<EventConfirmationResponse>> findAll() {
        return ResponseEntity.ok(eventConfirmationService.findAll());
    }
}
