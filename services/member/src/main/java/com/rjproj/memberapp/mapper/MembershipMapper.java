package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.model.Membership;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MembershipMapper {

    public Membership toMembership(@Valid MembershipRequest membershipRequest) {
        return Membership.builder()
                .membershipId(membershipRequest.membershipId())
                .organizationId(membershipRequest.organizationId())
                .member(membershipRequest.member())
                .membershipType(membershipRequest.membershipType())
                .status(membershipRequest.status())
                .startDate(membershipRequest.startDate())
                .endDate(membershipRequest.startDate())
                .build();
    }

    public MembershipResponse fromMembership(Membership membership) {
        return new MembershipResponse(
                membership.getMembershipId(),
                membership.getOrganizationId(),
                membership.getMember(),
                membership.getMembershipType(),
                membership.getStatus(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getCreatedAt(),
                membership.getUpdatedAt()
        );
    }
}
