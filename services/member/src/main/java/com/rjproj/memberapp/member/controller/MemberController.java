package com.rjproj.memberapp.member.controller;

import com.rjproj.memberapp.member.dto.MemberRequest;
import com.rjproj.memberapp.member.dto.MemberResponse;
import com.rjproj.memberapp.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<UUID> addMember(@RequestBody @Valid MemberRequest memberRequest){
        return ResponseEntity.ok(memberService.addMember(memberRequest));
    }

    @PutMapping
    public ResponseEntity<UUID> updateMember(@RequestBody @Valid MemberRequest memberRequest){
        memberService.updateMember(memberRequest);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMembers(){
        return ResponseEntity.ok(memberService.findAllMember());
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<MemberResponse> findById(
            @PathVariable("member-id") UUID memberId
    ) {
        return ResponseEntity.ok(memberService.findById(memberId));
    }

    @GetMapping("/exists/{member-id}")
    public ResponseEntity<Boolean> existById(@PathVariable("member-id") UUID memberId){
        return ResponseEntity.ok(memberService.existsById(memberId));
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable("member-id") UUID memberId
    ) {
        memberService.deleteMember(memberId);
        return ResponseEntity.accepted().build();
    }

}
