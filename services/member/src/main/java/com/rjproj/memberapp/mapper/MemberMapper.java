package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.model.Member;
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
                .password(memberRequest.password())
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
                member.getMemberAddress(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}