package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<String> addOrganization(@RequestBody OrganizationRequest organizationRequest){
        return ResponseEntity.ok(organizationService.addOrganization(organizationRequest));
    }

    @PutMapping
    public ResponseEntity<OrganizationResponse> updateOrganization(@RequestBody @Valid OrganizationRequest organizationRequest){
        OrganizationResponse updatedOrganizationResponse = organizationService.updateOrganization(organizationRequest);
        return new ResponseEntity(updatedOrganizationResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping("/viewAllOrganization")
    public ResponseEntity<List<OrganizationResponse>> getOrganizations(){
        return ResponseEntity.ok(organizationService.findAllOrganization());
    }

    @GetMapping("/viewMyOrganization")
    public ResponseEntity<List<OrganizationResponse>> getMyOrganizations(){
        return ResponseEntity.ok(organizationService.findMyOrganization());
    }

    @GetMapping("/{organization-id}")
    public ResponseEntity<OrganizationResponse> findOrganizationById(
            @PathVariable("organization-id") String organizationId
    ) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    @GetMapping("/exists/{organization-id}")
    public ResponseEntity<Boolean> existById(@PathVariable("organization-id") String organizationId){
        return ResponseEntity.ok(organizationService.existsById(organizationId));
    }

    @DeleteMapping("/{organization-id}")
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable("organization-id") String organizationId
    ) {
        organizationService.deleteOrganization(organizationId);
        return ResponseEntity.accepted().build();
    }
}
