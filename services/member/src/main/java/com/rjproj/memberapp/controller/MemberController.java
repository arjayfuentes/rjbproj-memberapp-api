package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

}