package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MembershipStatusRequest;
import com.rjproj.memberapp.dto.MembershipStatusResponse;
import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import com.rjproj.memberapp.model.MembershipStatus;
import com.rjproj.memberapp.model.MembershipType;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class MembershipStatusMapper {


    public MembershipStatus toMembershipStatus(@Valid MembershipStatusRequest membershipStatusRequest) {
        return MembershipStatus.builder()
                .membershipStatusId(membershipStatusRequest.membershipStatusId())
                .name(membershipStatusRequest.name())
                .description(membershipStatusRequest.description())
                .build();
    }

    public MembershipStatusResponse fromMembershipStatus(MembershipStatus membershipStatus) {
        return new MembershipStatusResponse(
                membershipStatus.getMembershipStatusId(),
                membershipStatus.getName(),
                membershipStatus.getDescription()
        );
    }


    public MembershipStatusRequest fromMembershipStatusToMembershipStatusRequest(MembershipStatus membershipStatus) {
        return new MembershipStatusRequest(
                membershipStatus.getMembershipStatusId(),
                membershipStatus.getName(),
                membershipStatus.getDescription()
        );
    }
}
