package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MemberAddressResponse;
import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.MemberAddress;
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
                .profilePicUrl(memberRequest.profilePicUrl())
                .memberAddress(memberRequest.memberAddress())
                .build();
    }

    public MemberResponse fromMember(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getProfilePicUrl(),
                fromMemberAddress(member.getMemberAddress()),
                member.getCreatedAt()
        );
    }


//    public MemberResponse fromMemberWithMembership(Member member) {
//        return new MemberResponse(
//                member.getMemberId(),
//                member.getFirstName(),
//                member.getLastName(),
//                member.getEmail(),
//                member.getPhoneNumber(),
//                null,
//                fromMemberAddress(member.getMemberAddress()),
//                member.getCreatedAt()
//        );
//    }


//    public Member fromMemberResponseToMember(MemberResponse memberResponse) {
//        return Member.builder()
//                .memberId(memberResponse.memberId())
//                .firstName(memberResponse.firstName())
//                .lastName(memberResponse.lastName())
//                .email(memberResponse.email())
//                .password(memberResponse.password())
//                .phoneNumber(memberResponse.phoneNumber())
//                .memberAddress(memberResponse.memberAddress())
//                .build();
//    }

    public MemberAddressResponse fromMemberAddress(MemberAddress memberAddress) {
        return new MemberAddressResponse(
                memberAddress.getStreet(),
                memberAddress.getCity(),
                memberAddress.getProvinceState(),
                memberAddress.getRegion(),
                memberAddress.getCountry()
        );
    }
}