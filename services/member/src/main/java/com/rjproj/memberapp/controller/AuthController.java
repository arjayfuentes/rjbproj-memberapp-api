package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.security.MemberDetails;
import com.rjproj.memberapp.service.MemberService;
import com.rjproj.memberapp.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestParam String userName, @RequestParam String password){
        return ResponseEntity.ok(memberService.login(userName, password));
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerMember(@RequestBody @Valid MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.registerMember(memberRequest));
    }
}
