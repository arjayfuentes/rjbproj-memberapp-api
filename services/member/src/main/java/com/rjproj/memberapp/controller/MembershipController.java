package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PutMapping(path = "/{membershipId}/approve")
    public ResponseEntity<MembershipResponse> approveMembershipRequest(@PathVariable("membershipId") UUID membershipId, @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.approveMembershipRequest(membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    /* Call from other service */
    @PostMapping("/current")
    public ResponseEntity<MembershipResponse> createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated User: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());

        return ResponseEntity.ok(membershipService.createMembershipForCurrentMember(createMembershipRequest));
    }

    @DeleteMapping(path = "/organizations/{organizationId}/memberships/{membershipId}")
    public ResponseEntity<UUID> deleteMembershipFromOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("membershipId") UUID membershipId
    ){
        return new ResponseEntity<>(
                membershipService.deleteMembershipFromOrganization(organizationId, membershipId),
                HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/{membershipId}/deny")
    public ResponseEntity<MembershipResponse> denyMembershipRequest(@PathVariable("membershipId") UUID membershipId, @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.denyMembershipRequest(membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByMemberId( @PathVariable("memberId") UUID memberId) {
        return ResponseEntity.ok(membershipService.getMembershipsByMemberId(memberId));
    }

    @GetMapping("/organizations/{organizationId}/members/{memberId}")
    public ResponseEntity<MembershipResponse> getMembershipByMemberIdAndOrganizationId(
            @PathVariable("organizationId") UUID organizationId,
            @PathVariable("memberId") UUID memberId
    ) {
        return ResponseEntity.ok(membershipService.getMembershipByMemberIdAndOrganizationId(memberId,organizationId));
    }

    @PostMapping("/organizations/{organizationId}/members")
    public ResponseEntity<Page<MembershipResponse>> getMembershipsByOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder,
            @RequestBody(required = false) MembershipFilters membershipFilters) {
        return ResponseEntity.ok(membershipService.getMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder, membershipFilters));
    }

    @PostMapping("/organizations/{organizationId}/members/pending")
    public ResponseEntity<Page<MembershipResponse>> getPendingMembershipsByOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "memberId", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "ASC", required = false) String sortOrder,
            @RequestBody(required = false) MembershipFilters membershipFilters) {
        return ResponseEntity.ok(membershipService.getPendingMembershipsByOrganization(organizationId, pageNo, pageSize, sortField, sortOrder, membershipFilters));
    }

    @PostMapping(path = "/request")
    public ResponseEntity<MembershipResponse> requestMembership(@RequestBody @Valid JoinOrganizationRequest organizationRequest) {
        return ResponseEntity.ok(membershipService.requestMembership(organizationRequest));
    }

    @PutMapping(path = "/{membershipId}")
    public ResponseEntity<MembershipResponse> updateMembership(@PathVariable("membershipId") UUID membershipId, @RequestBody @Valid MembershipRequest membershipRequest){
        return new ResponseEntity<>(
                membershipService.updateMembership(membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

}
