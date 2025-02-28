package com.rjproj.memberapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.CreateOrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping(path="/completeCreateOrganization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationResponse> createOrganization(
            @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
            @RequestPart(value = "backgroundImage", required = false) MultipartFile backgroundImage,
            @RequestPart(value = "createOrganizationRequest") String rawRequest) {

        ObjectMapper objectMapper = new ObjectMapper();
        CreateOrganizationRequest createOrganizationRequest;

        try {
            createOrganizationRequest = objectMapper.readValue(rawRequest, CreateOrganizationRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(organizationService.createOrganization(logoImage, backgroundImage, createOrganizationRequest));
    }

    @GetMapping("/findMyOrganizationById/{organizationId}")
    public ResponseEntity<OrganizationResponse> findMyOrganizationById(
            @PathVariable("organizationId") String organizationId
    ) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    /*from other service*/
    @GetMapping("/findOrganizationById/{organizationId}")
    public ResponseEntity<OrganizationResponse> findOrganizationById(
            @PathVariable("organizationId") String organizationId
    ) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    /*from other service*/
    @PostMapping("/findOrganizationsByIds")
    public ResponseEntity<List<OrganizationResponse>> findOrganizationsByIds(
            @RequestBody @Valid List<String> organizationIds
    ){
        return ResponseEntity.ok(organizationService.findOrganizationsByIds(organizationIds));
    }

    @GetMapping("/getAllOrganizations")
    public Page<OrganizationResponse> getOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String countryName,
            @RequestParam(required = false) String cityName
    ) {
        return organizationService.getOrganizations(page, size, name, countryName, cityName);
    }

    @GetMapping("getOrganizationsByMemberId/members/{memberId}")
    public ResponseEntity<List<OrganizationResponse>> getOrganizationsByMemberId( @PathVariable("memberId") UUID memberId) {
        return ResponseEntity.ok(organizationService.getOrganizationsByMemberId(memberId));
    }

    // Endpoint to get unique countries
    @GetMapping("/organizationCountries")
    public List<String> getUniqueOrganizationCountries() {
        return organizationService.getUniqueOrganizationCountries();
    }

    @PutMapping("/updateOrganization")
    public ResponseEntity<OrganizationResponse> updateOrganization(@RequestBody @Valid OrganizationRequest organizationRequest){
        OrganizationResponse updatedOrganizationResponse = organizationService.updateOrganization(organizationRequest);
        return new ResponseEntity(updatedOrganizationResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping(path="/updateOrganizationPhoto/{organizationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationResponse> updateOrganizationPhoto(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam("imageType") String imageType,
            @PathVariable("organizationId") String organizationId
    ) {
        return ResponseEntity.ok(organizationService.updateOrganizationPhoto(organizationId, image, imageType));
    }

}
