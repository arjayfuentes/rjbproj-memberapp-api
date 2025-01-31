package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody @Valid LoginRequest loginRequest){
        return memberService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerMember(@RequestBody @Valid MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.registerMember(memberRequest));
    }

    @PostMapping("/selectLoginOrganization")
    public ResponseEntity<?> selectLoginOrganization(@RequestBody @Valid SelectOrganizationLoginRequest selectOrganizationLoginRequest) {
        return memberService.selectLoginOrganization(selectOrganizationLoginRequest);
    }


}