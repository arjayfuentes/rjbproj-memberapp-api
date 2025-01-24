package com.rjproj.memberapp.member.service;

import com.rjproj.memberapp.exception.MemberNotFoundException;
import com.rjproj.memberapp.exception.MembershipNotFoundException;
import com.rjproj.memberapp.member.dto.MemberRequest;
import com.rjproj.memberapp.member.dto.MemberResponse;
import com.rjproj.memberapp.member.dto.MembershipRequest;
import com.rjproj.memberapp.member.dto.MembershipResponse;
import com.rjproj.memberapp.member.mapper.MembershipMapper;
import com.rjproj.memberapp.member.model.Member;
import com.rjproj.memberapp.member.model.Membership;
import com.rjproj.memberapp.member.repository.MembershipRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipMapper membershipMapper;

    public UUID addMembership(@Valid MembershipRequest membershipRequest) {
        Membership membership = membershipRepository.save(membershipMapper.toMembership(membershipRequest));
        return membership.getMembershipId();
    }

    public void updateMember(@Valid MembershipRequest membershipRequest) {
        Membership membership = membershipRepository.findById(membershipRequest.membershipId())
                .orElseThrow(() -> new MembershipNotFoundException(
                        String.format("Cannot update membership with id %s", membershipRequest.membershipId())
                ));
        mergeMembership(membership, membershipRequest);
        membershipRepository.save(membership);
    }

    private void mergeMembership(Membership membership, @Valid MembershipRequest membershipRequest) {
        if(membershipRequest.membershipId() != null) {
            membership.setMembershipId(membershipRequest.membershipId());
        }
        if(membershipRequest.organizationId() != null) {
            membership.setOrganizationId(membershipRequest.organizationId());
        }
        if(membershipRequest.membershipTypeId() != null) {
            membership.setMembershipTypeId(membershipRequest.membershipTypeId());
        }
        if(StringUtils.isNotBlank(membershipRequest.status())) {
            membership.setStatus(membershipRequest.status());
        }
        if(membershipRequest.startDate() != null) {
            membership.setStartDate(membershipRequest.startDate());
        }
        if(membershipRequest.endDate() != null) {
            membership.setEndDate(membershipRequest.endDate());
        }
    }

    public List<MembershipResponse> findAllMember() {
        return membershipRepository.findAll()
                .stream()
                .map(membershipMapper::fromMembership)
                .collect(Collectors.toList());
    }

    public MembershipResponse findById(UUID membershipId) {
        return membershipRepository.findById(membershipId)
                .map(membershipMapper::fromMembership)
                .orElseThrow(() -> new MemberNotFoundException(
                        String.format("No membership found with the provided ID: %s", membershipId))
                );
    }

    public Boolean existsById(UUID membershipId) {
        return membershipRepository.findById(membershipId)
                .isPresent();
    }

    public void deleteMember(UUID membershipId) {
        membershipRepository.deleteById(membershipId);
    }

}
