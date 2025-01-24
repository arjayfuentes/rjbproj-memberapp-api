package com.rjproj.memberapp.member.controller;


import com.rjproj.memberapp.member.dto.MembershipRequest;
import com.rjproj.memberapp.member.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    public ResponseEntity<UUID> addMembership(@RequestBody @Valid MembershipRequest membershipRequest){
        return ResponseEntity.ok(membershipService.addMembership(membershipRequest));
    }
}
