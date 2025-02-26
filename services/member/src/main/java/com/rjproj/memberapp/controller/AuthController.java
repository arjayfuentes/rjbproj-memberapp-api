package com.rjproj.memberapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/getLoginSession")
    public ResponseEntity<Session> getLoginSession(@RequestBody @Valid String token){
        return ResponseEntity.ok(memberService.getLoginSession(token));
    }

    @PostMapping("/login")
    public ResponseEntity<Session> loginMember(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(memberService.loginMember(loginRequest));
    }

    @PostMapping("/login/withGoogle")
    public ResponseEntity<Session> loginWithGoogle(@RequestBody @Valid GoogleRequest googleRequest){
        return ResponseEntity.ok(memberService.loginMemberWithGoogle(googleRequest.googleToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerMember(@RequestBody @Valid MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.registerMember(memberRequest));
    }

    @PostMapping("/register/withGoogle")
    public ResponseEntity<MemberResponse> registerMemberWithGoole(@RequestBody @Valid GoogleRequest googleRequest) {
        return ResponseEntity.ok(memberService.registerMemberWithGoogle(googleRequest.googleToken()));
    }

    @PostMapping("/selectLoginOrganization")
    public ResponseEntity<Session> selectLoginOrganization(@RequestBody @Valid SelectOrganizationLoginRequest selectOrganizationLoginRequest) {
        return ResponseEntity.ok(memberService.selectLoginOrganization(selectOrganizationLoginRequest));
    }

    @PostMapping(path = "/register/updateMemberAfterRegistration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponse> updateMemberAfterRegistration(
            @RequestPart(value = "profilePicImage", required = false) MultipartFile profilePicImage,
            @RequestPart(value = "additionalInfoRequest") String additionalInfoRequest
    ) {

        AdditionalInfoRequest request;
        try {
            request = objectMapper.readValue(additionalInfoRequest, AdditionalInfoRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(memberService.updateMemberAfterRegistration(profilePicImage, request));
    }

}