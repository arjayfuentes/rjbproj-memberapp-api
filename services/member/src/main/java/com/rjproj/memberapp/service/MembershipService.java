package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.repository.MembershipRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MemberRepository memberRepository;

    private final MembershipRepository membershipRepository;

    private final MembershipMapper membershipMapper;

    public MembershipResponse createMembership(@Valid MembershipRequest membershipRequest) {
//        Member member = memberRepository.findById(membershipRequest.memberId())
//                .orElseThrow(() -> new EntityNotFoundException("Membership not found with ID:: " + membershipRequest.membershipId()));;
//
        Membership membership = membershipMapper.toMembership(membershipRequest);
        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    public List<MembershipResponse> findAll() {
        return membershipRepository.findAll()
                .stream()
                .map(membershipMapper::fromMembership)
                .collect(Collectors.toList());
    }

    public MembershipResponse findById(UUID membershipId) {
        return membershipRepository.findById(membershipId)
                .map(membershipMapper::fromMembership)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with ID:: " + membershipId));
    }


    public MembershipResponse updateMembership(UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update membership with id %s", membershipId.toString())
                ));
        mergeMembership(membership, membershipRequest);
        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }


    public void deleteMembership(UUID membershipId) {
        membershipRepository.deleteById(membershipId);
    }


    private void mergeMembership(Membership membership, @Valid MembershipRequest membershipRequest) {
        if(membershipRequest.organizationId() != null) {
            membership.setOrganizationId(membershipRequest.organizationId());
        }
        if(membershipRequest.member() != null) {
            membership.setMember(membershipRequest.member());
        }
        if(membershipRequest.membershipType() != null) {
            membership.setMembershipType(membershipRequest.membershipType());
        }
        if(StringUtils.isNotBlank(membershipRequest.status())) {
            membership.setStatus(membershipRequest.status());
        }
        if(membershipRequest.startDate() != null) {
            membership.setMember(membershipRequest.member());
        }
        if(membershipRequest.endDate() != null) {
            membership.setEndDate(membershipRequest.endDate());
        }

    }
}
