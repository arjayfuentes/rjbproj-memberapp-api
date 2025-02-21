package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.mapper.MembershipStatusMapper;
import com.rjproj.memberapp.mapper.MembershipTypeMapper;
import com.rjproj.memberapp.mapper.RoleMapper;
import com.rjproj.memberapp.model.*;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.repository.*;
import com.rjproj.memberapp.security.JWTUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MemberRepository memberRepository;

    private final MembershipRepository membershipRepository;

    private final MembershipTypeRepository membershipTypeRepository;

    private final MembershipStatusRepository membershipStatusRepository;

    private final MembershipMapper membershipMapper;

    private final MembershipTypeMapper membershipTypeMapper;

    private final MembershipStatusMapper membershipStatusMapper;

    private final RoleMapper roleMapper;

    private final OrganizationClient organizationClient;

    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MemberRoleRepository memberRoleRepository;


    public MembershipResponse createMembership(@Valid MembershipRequest membershipRequest) {
        Membership membership = membershipMapper.toMembership(membershipRequest);
        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    public MembershipResponse requestMembership(@Valid JoinOrganizationRequest joinOrganizationRequest) {
        Member member = memberRepository.findById(joinOrganizationRequest.memberId())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with ID:: " + joinOrganizationRequest.memberId()));
        OrganizationResponse organizationResponse = this.organizationClient.findOrganizationById(joinOrganizationRequest.organizationId());
        if(organizationResponse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Organization not found with ID:: " + joinOrganizationRequest.organizationId());
        }
        MembershipStatus membershipStatus = membershipStatusRepository.findByName("Pending");
        MembershipRequest membershipRequest = new MembershipRequest(
                null,
                joinOrganizationRequest.organizationId(),
                member,
                null,
                membershipStatusMapper.fromMembershipStatusToMembershipStatusRequest(membershipStatus),
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


    //because inside is update member role which is transactional
    @Transactional
    public MembershipResponse updateMembership(UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Optional<Membership> membershipOptional = membershipRepository.findByMembershipId(membershipRequest.membershipId());
        if (membershipOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found with ID: " + membershipRequest.membershipId());
        }
        Membership membership = membershipOptional.get();

        Optional<Role> roleOptional = roleRepository.findById(membershipRequest.role().roleId());
        if (roleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with ID: " + membershipRequest.role().roleId());
        }
        Role role = roleOptional.get();

        Optional<MemberRole> memberRoleOptional = memberRoleRepository.findByMemberIdAndOrganizationId(
                membershipRequest.member().getMemberId(), membershipRequest.organizationId());

        if (memberRoleOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MemberRole not found for memberId: "
                    + membershipRequest.member().getMemberId() + " and organizationId: " + membershipRequest.organizationId());
        }

        memberRoleRepository.updateMemberRole(
                memberRoleOptional.get().getId().getMemberId(),
                memberRoleOptional.get().getId().getOrganizationId(),
                role.getRoleId()
        );

        Optional<MemberRole> updatedMemberRole = memberRoleRepository.findByMemberIdAndOrganizationId(
                membershipRequest.member().getMemberId(), membershipRequest.organizationId());

        if (updatedMemberRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update MemberRole.");
        }

        mergeMembership(membership, membershipRequest);
        membershipRepository.save(membership);

        return membershipMapper.fromMembershipWithRole(membership, updatedMemberRole.get().getRole());
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

        if(membershipRequest.membershipStatus() != null) {
            membership.setMembershipStatus(membershipStatusMapper.toMembershipStatus(membershipRequest.membershipStatus()));
        }
        if(membershipRequest.startDate() != null) {
            membership.setStartDate(membershipRequest.startDate());
        }
        if(membershipRequest.endDate() != null) {
            membership.setEndDate(membershipRequest.endDate());
        }

    }

    public List<OrganizationResponse> getOrganizationByMemberId(UUID memberId) {
        List<UUID> organizationIdsAsStrings = getActiveOrganizationIdsByMemberId(memberId);

        try {
            List<OrganizationResponse> response = this.organizationClient.findOrganizationsByIds(organizationIdsAsStrings)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));
            System.out.println("Received Response: " + response);  // Debugging
            return response;
        } catch (Exception e) {
            e.printStackTrace();  // Log full stack trace
            throw new RuntimeException(e);
        }

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

    public MembershipResponse createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest) {
        UUID memberId = jwtUtil.extractMemberIdInternally();
        return createMembershipByOrganizationIdAndMemberId(createMembershipRequest.organizationId(), memberId, createMembershipRequest.membershipTypeId());

    }

    public MembershipResponse createMembershipByOrganizationIdAndMemberId(UUID organizationId, UUID memberId, UUID membershipTypeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Cannot update member with id %s", memberId)
                ));

        Optional<MembershipType> membershipType = membershipTypeRepository.findById(membershipTypeId);

        MembershipStatus membershipStatus = membershipStatusRepository.findByName("Active");

        Membership membership = Membership.builder()
                .member(member)
                .organizationId(organizationId)
                .membershipType(membershipType.get())
                .membershipStatus(membershipStatus)
                .startDate(new Timestamp(System.currentTimeMillis()))
                .endDate(null)
                .build();

        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    public MembershipResponse updateMembershipType(UUID membershipId, @Valid MembershipRequest membershipRequest) {
        System.out.println("Received membershipId: " + membershipRequest.membershipId());

        Optional<Membership> membershipOpt = membershipRepository.findByMembershipId(membershipRequest.membershipId());
        System.out.println("Membership found: " + membershipOpt.isPresent()); // Check if membership is found

        if(membershipOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Membership not found with ID: " + membershipRequest.membershipId());
        }
        membershipOpt.get().setMembershipType(membershipRequest.membershipType());

        MembershipStatus membershipStatus = membershipStatusRepository.findByName("Active");

        membershipOpt.get().setMembershipStatus(membershipStatus);
        membershipOpt.get().setStartDate(Timestamp.from(Instant.now()));
        membershipOpt.get().setEndDate(getEndDate(membershipRequest.membershipType()));

        //set Member role as default
        Optional<Role> role = roleRepository.findByName("Member");
        if(role.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found");
        }
        Optional<Member> member = memberRepository.findById(membershipOpt.get().getMember().getMemberId());
        if(member.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Member not found with ID: " + membershipOpt.get().getMembershipId());
        }

        MemberRoleId memberRoleId = new MemberRoleId();
        memberRoleId.setMemberId(member.get().getMemberId());
        memberRoleId.setRoleId(role.get().getRoleId());
        memberRoleId.setOrganizationId(membershipRequest.organizationId());


        MemberRole memberRole = MemberRole.builder().id(memberRoleId).member(member.get()).role(role.get()).build();

        memberRoleRepository.save(memberRole);
        return membershipMapper.fromMembership(membershipRepository.save(membershipOpt.get()));
    }


    private Timestamp getEndDate(MembershipType membershipType) {
        Timestamp endDate = null;
        if(membershipType.getMembershipTypeValidity().getName().equals("Ends after 1 year")) {
            LocalDateTime oneYearLater = LocalDateTime.now().plusYears(1);
            endDate = Timestamp.valueOf(oneYearLater);
        } else if(membershipType.getMembershipTypeValidity().getName().equals("Ends on January 1")) {
            LocalDateTime januaryFirstNextYearLocal = LocalDateTime.now()
                    .plusYears(1) // Move to next year
                    .withMonth(Month.JANUARY.getValue()) // Set the month to January
                    .withDayOfMonth(1) // Set the day to 1
                    .withHour(0) // Set hour to 00:00
                    .withMinute(0) // Set minute to 00
                    .withSecond(0) // Set second to 00
                    .withNano(0); // Set nanosecond to 0

            endDate = Timestamp.valueOf(januaryFirstNextYearLocal);
        } else {
            endDate = null;
        }
        return endDate;
    }
}
