package com.rjproj.memberapp.member.mapper;

import com.rjproj.memberapp.member.dto.MembershipRequest;
import com.rjproj.memberapp.member.dto.MembershipResponse;
import com.rjproj.memberapp.member.model.Member;
import com.rjproj.memberapp.member.model.Membership;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MembershipMapper {

    public Membership toMembership(@Valid MembershipRequest membershipRequest) {
        return Membership.builder()
                .membershipId(membershipRequest.membershipId())
                .memberId(membershipRequest.memberId())
                .organizationId(membershipRequest.organizationId())
                .membershipTypeId(membershipRequest.membershipTypeId())
                .status(membershipRequest.status())
                .startDate(membershipRequest.startDate())
                .endDate(membershipRequest.endDate())
                .build();
    }

    public MembershipResponse fromMembership(Membership membership) {
        return new MembershipResponse(
                membership.getMembershipId(),
                membership.getMemberId(),
                membership.getOrganizationId(),
                membership.getMembershipTypeId(),
                membership.getStatus(),
                membership.getStartDate(),
                membership.getEndDate()
        );
    }
}
