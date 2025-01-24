package com.rjproj.memberapp.member;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MemberMapper {

    public Member toMember(@Valid MemberRequest memberRequest) {
        return Member.builder()
                .memberId(memberRequest.memberId())
                .firstName(memberRequest.firstName())
                .lastName(memberRequest.lastName())
                .email(memberRequest.email())
                .phoneNumber(memberRequest.phoneNumber())
                .memberAddress(memberRequest.memberAddress())
                .build();
    }

    public MemberResponse fromMember(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPassword(),
                member.getPhoneNumber(),
                member.getMemberAddress()
        );
    }
}
