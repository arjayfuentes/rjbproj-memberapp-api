package com.rjproj.memberapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.AdditionalInfoRequest;
import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody @Valid MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.createMember(memberRequest));
    }

    @PutMapping(path = "/{member-id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable("member-id") UUID memberId, @RequestBody @Valid MemberRequest memberRequest){
        //return ResponseEntity.ok(memberService.updateMember(memberRequest));
        return new ResponseEntity<>(
                memberService.updateMember(memberId, memberRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<MemberResponse> findById(
            @PathVariable("member-id") UUID memberId
    ) {
        return ResponseEntity.ok(memberService.findById(memberId));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable("member-id") UUID memberId
    ) {
        memberService.deleteMember(memberId);
        return ResponseEntity.accepted().build();
    }


    @PostMapping("/createDefaultAdminOrganizationRoleForOwner")
    public ResponseEntity<String> createDefaultAdminOrganizationRoleForOwner(@RequestBody @Valid UUID organizationId) {
        return ResponseEntity.ok(memberService.createDefaultAdminOrganizationRoleForOwner(organizationId));
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<MemberResponse>> getMembersByOrganization(
            @PathVariable UUID organizationId) {
        return ResponseEntity.ok(memberService.getMembersByOrganization(organizationId));
    }

    @GetMapping("/organizationPage/{organizationId}")
    public ResponseEntity<Page<MemberResponse>> getMembersByOrganizationPage(
            @PathVariable UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder) {
        return ResponseEntity.ok(memberService.getMembersByOrganizationPaginationAndSorting(organizationId, pageNo, pageSize, sortField, sortOrder));
    }

    @GetMapping("/organization/{organizationId}/memberships")
    public ResponseEntity<Page<MembershipResponse>> getMembershipsByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder) {
        return ResponseEntity.ok(memberService.getMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder));
    }

    @GetMapping("/organization/{organizationId}/memberships/pending")
    public ResponseEntity<Page<MembershipResponse>> getPendingMembershipsByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder) {
        return ResponseEntity.ok(memberService.getPendingMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder));
    }


    @PostMapping(path = "/updateMemberDetails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponse> updateMemberDetails(
            @RequestPart(value = "profilePicImage", required = false) MultipartFile profilePicImage,
            @RequestPart(value = "additionalInfoRequest") String additionalInfoRequest
    ) {

        AdditionalInfoRequest request;
        try {
            request = objectMapper.readValue(additionalInfoRequest, AdditionalInfoRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(memberService.updateMemberDetails(profilePicImage, request));
    }


}