package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.GetMembershipRequest;
import com.rjproj.memberapp.dto.JoinOrganizationRequest;
import com.rjproj.memberapp.dto.MembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.service.MemberService;
import com.rjproj.memberapp.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<MembershipResponse> createMembership(@RequestBody @Valid MembershipRequest membershipRequest) {
        return ResponseEntity.ok(membershipService.createMembership(membershipRequest));
    }

    @PostMapping(path = "/requestMembership")
    public ResponseEntity<MembershipResponse> requestMembership(@RequestBody @Valid JoinOrganizationRequest organizationRequest) {
        return ResponseEntity.ok(membershipService.requestMembership(organizationRequest));
    }

    @PutMapping(path = "/{membership-id}")
    public ResponseEntity<MembershipResponse> updateMembership(@PathVariable("membership-id") UUID membershipId, @RequestBody @Valid MembershipRequest membershipRequest){
        //return ResponseEntity.ok(membershipService.updateMembership(membershipRequest));
        return new ResponseEntity<>(
                membershipService.updateMembership(membershipId, membershipRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/{membership-id}")
    public ResponseEntity<MembershipResponse> findById(
            @PathVariable("membership-id") UUID membershipId
    ) {
        return ResponseEntity.ok(membershipService.findById(membershipId));
    }

    @PostMapping("/getMembershipByMemberIdAndOrganizationId")
    public ResponseEntity<MembershipResponse> getMembershipByMemberIdAndOrganizationId(
            @RequestBody @Valid GetMembershipRequest getMembershipRequest
    ) {
        return ResponseEntity.ok(membershipService.getMembershipByMemberIdAndOrganizationId(getMembershipRequest.memberId(),getMembershipRequest.organizationId()));
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

    @GetMapping("/getOrganizationByMemberId/{member-id}")
    public ResponseEntity<List<OrganizationResponse>> getOrganizationByMemberId( @PathVariable("member-id") UUID memberId) {
        return ResponseEntity.ok(membershipService.getOrganizationByMemberId(memberId));
    }


}
