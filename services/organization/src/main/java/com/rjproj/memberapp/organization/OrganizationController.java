package com.rjproj.memberapp.organization;

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
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestBody @Valid OrganizationRequest organizationRequest) {
        return ResponseEntity.ok(organizationService.createOrganization(organizationRequest));
    }

    @PutMapping
    public ResponseEntity<OrganizationResponse> updateOrganization(@RequestBody @Valid OrganizationRequest organizationRequest){
        return ResponseEntity.ok(organizationService.updateOrganization(organizationRequest));
    }

    @GetMapping("/{organization-id}")
    public ResponseEntity<OrganizationResponse> findById(
            @PathVariable("organization-id") UUID organizationId
    ) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> findAll() {
        return ResponseEntity.ok(organizationService.findAll());
    }




}
