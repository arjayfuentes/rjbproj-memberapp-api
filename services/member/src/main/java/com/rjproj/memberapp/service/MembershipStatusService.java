package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MembershipStatusResponse;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import com.rjproj.memberapp.mapper.MembershipStatusMapper;
import com.rjproj.memberapp.mapper.MembershipTypeMapper;
import com.rjproj.memberapp.model.MembershipStatus;
import com.rjproj.memberapp.model.Role;
import com.rjproj.memberapp.repository.MembershipStatusRepository;
import com.rjproj.memberapp.repository.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipStatusService {

    private final MembershipStatusRepository membershipStatusRepository;

    private final MembershipStatusMapper membershipStatusMapper;

    public List<MembershipStatusResponse> getMemberMembershipStatuses() {
        List<String> memberMembershipStatuses = new ArrayList<>();
        memberMembershipStatuses.add("Active");
        memberMembershipStatuses.add("Expired");
        memberMembershipStatuses.add("Cancelled");
        List<MembershipStatus> membershipStatuses = membershipStatusRepository.findByNameIn(memberMembershipStatuses);
        return membershipStatuses.stream().map(membershipStatusMapper::fromMembershipStatus).collect(Collectors.toList()) ;
    }

}
