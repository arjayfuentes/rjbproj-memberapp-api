package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.GetMembershipRequest;
import com.rjproj.memberapp.dto.JoinOrganizationRequest;
import com.rjproj.memberapp.dto.MembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
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

    private final OrganizationClient organizationClient;


    public MembershipResponse createMembership(@Valid MembershipRequest membershipRequest) {
        Membership membership = membershipMapper.toMembership(membershipRequest);
        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    public MembershipResponse requestMembership(@Valid JoinOrganizationRequest joinOrganizationRequest) {
        Member member = memberRepository.findById(joinOrganizationRequest.memberId())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with ID:: " + joinOrganizationRequest.memberId()));
        OrganizationResponse organizationResponse = this.organizationClient.findMyOrganizationById(joinOrganizationRequest.organizationId());
        if(organizationResponse == null) {
            throw new NotFoundException("Organization not found with ID:: " + joinOrganizationRequest.organizationId());
        }
        MembershipRequest membershipRequest = new MembershipRequest(
                null,
                joinOrganizationRequest.organizationId(),
                member,
                null,
                null,
                null,
                null
        );
        return createMembership(membershipRequest);
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

    public List<OrganizationResponse> getOrganizationByMemberId(UUID memberId) {
        List<UUID> organizationIdsAsStrings = getActiveOrganizationIdsByMemberId(memberId);

        return this.organizationClient.findOrganizationsByIds(organizationIdsAsStrings)
                .orElseThrow(() -> new NotFoundException("Organization not found"));
    }

    public List<UUID> getOrganizationIdsByMemberId(UUID memberId) {
        return membershipRepository.findOrganizationIdsByMemberId(memberId);
    }

    public List<UUID> getActiveOrganizationIdsByMemberId(UUID memberId) {
        return membershipRepository.findActiveOrganizationIdsByMemberId(memberId);
    }

    public Membership getMembership(UUID memberId, UUID organizationId) {
          return membershipRepository.findMembershipByMemberIdAndOrganizationId(memberId, organizationId);
    }

    public MembershipResponse getMembershipByMemberIdAndOrganizationId(UUID memberId, UUID organizationId) {
        Membership membership = membershipRepository.findMembershipByMemberIdAndOrganizationId(memberId, organizationId);
        return membershipMapper.fromMembership(membership);
    }
}
