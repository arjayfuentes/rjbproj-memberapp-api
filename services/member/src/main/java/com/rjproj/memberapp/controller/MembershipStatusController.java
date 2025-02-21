package com.rjproj.memberapp.controller;


import com.rjproj.memberapp.dto.MembershipStatusResponse;
import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.model.MembershipStatus;
import com.rjproj.memberapp.service.MembershipStatusService;
import com.rjproj.memberapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership-status")
@RequiredArgsConstructor
public class MembershipStatusController {

    private final MembershipStatusService membershipStatusService;

    @GetMapping("/getMemberMembershipStatuses")
    public ResponseEntity<List<MembershipStatusResponse>> getMemberMembershipStatuses() {
        return ResponseEntity.ok(membershipStatusService.getMemberMembershipStatuses());
    }
}
