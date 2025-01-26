package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<UUID> addOrganization(@RequestBody @Valid OrganizationRequest organizationRequest){
        return ResponseEntity.ok(organizationService.addOrganization(organizationRequest));
    }

    @PutMapping
    public ResponseEntity<UUID> updateOrganization(@RequestBody @Valid OrganizationRequest organizationRequest){
        organizationService.updateOrganization(organizationRequest);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getOrganizations(){
        return ResponseEntity.ok(organizationService.findAllOrganization());
    }

    @GetMapping("/{organization-id}")
    public ResponseEntity<OrganizationResponse> findById(
            @PathVariable("organization-id") UUID organizationId
    ) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    @GetMapping("/exists/{organization-id}")
    public ResponseEntity<Boolean> existById(@PathVariable("organization-id") UUID organizationId){
        return ResponseEntity.ok(organizationService.existsById(organizationId));
    }

    @DeleteMapping("/{organization-id}")
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable("organization-id") UUID organizationId
    ) {
        organizationService.deleteOrganization(organizationId);
        return ResponseEntity.accepted().build();
    }
}
