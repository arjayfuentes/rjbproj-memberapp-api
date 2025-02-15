package com.rjproj.memberapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.CreateOrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.model.File;
import com.rjproj.memberapp.model.ImageMetadata;
import com.rjproj.memberapp.service.FileService;
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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    private final FileService fileService;


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
    public ResponseEntity<List<OrganizationResponse>> viewAllOrganizations(){
        return ResponseEntity.ok(organizationService.findAllOrganization());
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

    // Endpoint to get unique countries
    @GetMapping("/organizationCountries")
    public List<String> getUniqueOrganizationCountries() {
        return organizationService.getUniqueOrganizationCountries();
    }

    @GetMapping("/organizationCitiesByCountry")
    public List<String> getUniqueOrganizationCitiesByCountry(
            @RequestParam String country
    ) {
        return organizationService.getCitiesByCountry(country);
    }

    @GetMapping("/saveOrganizations")
    public ResponseEntity<List<OrganizationResponse>> saveOrganizations(
            @RequestBody List<OrganizationRequest> organizationRequests
    ){
        return ResponseEntity.ok(organizationService.saveOrganizations(organizationRequests));
    }

//    @GetMapping("/viewMyOrganization/{member-id}")
//    public ResponseEntity<List<OrganizationResponse>> getMyOrganizations(
//            @RequestBody @Valid OrganizationRequest organizationRequest
//    ){
//        return ResponseEntity.ok(organizationService.findMyOrganization(memberId));
//    }

    @PostMapping("/findOrganizationsByIds")
    public ResponseEntity<List<OrganizationResponse>> findOrganizationsByIds(
            @RequestBody @Valid List<String> organizationIds
    ){
        return ResponseEntity.ok(organizationService.findOrganizationsByIds(organizationIds));
    }

    @GetMapping("/findOrganizationById/{organization-id}")
    public ResponseEntity<OrganizationResponse> findOrganizationById(
            @PathVariable("organization-id") String organizationId
    ) {
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }


    @GetMapping("/findMyOrganizationById/{organization-id}")
    public ResponseEntity<OrganizationResponse> findMyOrganizationById(
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

    @PostMapping("/upload")
    public ResponseEntity<Object> saveFIle(@RequestParam(required = false) MultipartFile file,
                                           @RequestParam(required = false) String name){
        if (file.isEmpty() || name.isEmpty()){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File and Name are required");
        }
        return ResponseEntity.ok(fileService.saveFile(file, name));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<File>> getAllFiles(){
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @PostMapping(path="/completeCreateOrganization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationResponse> completeCreateOrganization(
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

        return ResponseEntity.ok(organizationService.completeCreateOrganization(logoImage, backgroundImage, createOrganizationRequest));
    }



}
