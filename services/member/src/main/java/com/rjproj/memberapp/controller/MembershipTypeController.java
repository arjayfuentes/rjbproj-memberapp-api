package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import com.rjproj.memberapp.service.MembershipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership-type")
@RequiredArgsConstructor
public class MembershipTypeController {

    private final MembershipTypeService membershipTypeService;

    @PostMapping
    public ResponseEntity<MembershipTypeResponse> createMembershipType(@RequestBody @Valid MembershipTypeRequest membershipTypeRequest) {
        return ResponseEntity.ok(membershipTypeService.createMembershipType(membershipTypeRequest));
    }

    @PutMapping(path = "/{membershipType-id}")
    public ResponseEntity<MembershipTypeResponse> updateMembershipType(@PathVariable("membershipType-id") UUID membershipTypeId, @RequestBody @Valid MembershipTypeRequest membershipTypeRequest){
        //return ResponseEntity.ok(membershipTypeService.updateMembershipType(membershipTypeRequest));
        return new ResponseEntity<>(
                membershipTypeService.updateMembershipType(membershipTypeId, membershipTypeRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/{membershipType-id}")
    public ResponseEntity<MembershipTypeResponse> findById(
            @PathVariable("membershipType-id") UUID membershipTypeId
    ) {
        return ResponseEntity.ok(membershipTypeService.findById(membershipTypeId));
    }

    @GetMapping
    public ResponseEntity<List<MembershipTypeResponse>> findAll() {
        return ResponseEntity.ok(membershipTypeService.findAll());
    }

    @DeleteMapping("/{membershipType-id}")
    public ResponseEntity<Void> deleteMembershipType(
            @PathVariable("membershipType-id") UUID membershipTypeId
    ) {
        membershipTypeService.deleteMembershipType(membershipTypeId);
        return ResponseEntity.accepted().build();
    }
}
