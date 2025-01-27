package com.rjproj.memberapp.event.controller;

import com.rjproj.memberapp.event.dto.EventRequest;
import com.rjproj.memberapp.event.dto.EventResponse;
import com.rjproj.memberapp.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventRequest evenRequest) {
        return ResponseEntity.ok(eventService.createEvent(evenRequest));
    }

    @PutMapping
    public ResponseEntity<EventResponse> updateOrganization(@RequestBody @Valid EventRequest evenRequest){
        return new ResponseEntity<>(
                eventService.updateEvent(evenRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/{event-id}")
    public ResponseEntity<EventResponse> findById(
            @PathVariable("event-id") UUID eventId
    ) {
        return ResponseEntity.ok(eventService.findById(eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }
}
