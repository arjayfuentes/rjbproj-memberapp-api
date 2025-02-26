package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    /* Call from other service */
    @PostMapping("/createMembershipForCurrentMember")
    public ResponseEntity<MembershipResponse> createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest) {
        return ResponseEntity.ok(membershipService.createMembershipForCurrentMember(createMembershipRequest));
    }

    @PostMapping("/getMembershipByMemberIdAndOrganizationId")
    public ResponseEntity<MembershipResponse> getMembershipByMemberIdAndOrganizationId(
            @RequestBody @Valid GetMembershipRequest getMembershipRequest
    ) {
        return ResponseEntity.ok(membershipService.getMembershipByMemberIdAndOrganizationId(getMembershipRequest.memberId(),getMembershipRequest.organizationId()));
    }

    @PostMapping("/organization/{organizationId}/memberships")
    public ResponseEntity<Page<MembershipResponse>> getMembershipsByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder,

            @RequestBody(required = false) MembershipFilters membershipFilters) {
        return ResponseEntity.ok(membershipService.getMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder, membershipFilters));
    }

    @GetMapping("/organization/{organizationId}/memberships/pending")
    public ResponseEntity<Page<MembershipResponse>> getPendingMembershipsByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder) {
        return ResponseEntity.ok(membershipService.getPendingMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder));
    }

    @GetMapping("/getOrganizationByMemberId/{member-id}")
    public ResponseEntity<List<OrganizationResponse>> getOrganizationByMemberId( @PathVariable("member-id") UUID memberId) {
        return ResponseEntity.ok(membershipService.getOrganizationByMemberId(memberId));
    }

    @PostMapping(path = "/requestMembership")
    public ResponseEntity<MembershipResponse> requestMembership(@RequestBody @Valid JoinOrganizationRequest organizationRequest) {
        return ResponseEntity.ok(membershipService.requestMembership(organizationRequest));
    }

    @PutMapping(path = "/updateMembership/{membership-id}")
    public ResponseEntity<MembershipResponse> updateMembership(@PathVariable("membership-id") UUID membershipId, @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.updateMembership(membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/updateMembershipType/{membership-id}")
    public ResponseEntity<MembershipResponse> updateMembershipType(@PathVariable("membership-id") UUID membershipId, @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.updateMembershipType(membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    /* Below unused methods */

    @PostMapping
    public ResponseEntity<MembershipResponse> createMembership(@RequestBody @Valid MembershipRequest membershipRequest) {
        return ResponseEntity.ok(membershipService.createMembership(membershipRequest));
    }


    @GetMapping("/{membership-id}")
    public ResponseEntity<MembershipResponse> findById(
            @PathVariable("membership-id") UUID membershipId
    ) {
        return ResponseEntity.ok(membershipService.findById(membershipId));
    }

    @GetMapping
    public ResponseEntity<List<MembershipResponse>> findAll() {
        return ResponseEntity.ok(membershipService.findAll());
    }

    @DeleteMapping("/{membership-id}")
    public ResponseEntity<Void> deleteMembership(
            @PathVariable("membership-id") UUID membershipId
    ) {
        membershipService.deleteMembership(membershipId);
        return ResponseEntity.accepted().build();
    }




}
