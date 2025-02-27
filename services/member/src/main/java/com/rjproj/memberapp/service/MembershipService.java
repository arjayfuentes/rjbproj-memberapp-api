package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.mapper.MembershipStatusMapper;
import com.rjproj.memberapp.model.*;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.repository.*;
import com.rjproj.memberapp.security.JWTUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    @Autowired
    JWTUtil jwtUtil;

    private final MemberRepository memberRepository;

    @Autowired
    private final MemberService memberService;

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    private final MembershipMapper membershipMapper;

    private final MembershipRepository membershipRepository;

    private final MembershipStatusMapper membershipStatusMapper;

    private final MembershipStatusRepository membershipStatusRepository;

    private final MembershipTypeRepository membershipTypeRepository;

    private final OrganizationClient organizationClient;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    public MembershipResponse approveMembershipRequest(UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Membership membership = getMembershipById(membershipId);

        membership.setMembershipType(membershipRequest.membershipType());

        setMembershipStatusByStatusName(membership, membershipRequest.membershipType(), "Active");

        Role role = roleService.getRoleByName("Member");

        Member member = memberService.getMemberById(membership.getMember().getMemberId());

        Optional<MemberRole> existingMemberRoleOpt = memberRoleRepository.findByMemberIdAndOrganizationId(membership.getMember().getMemberId(), membershipRequest.organizationId());

        if(existingMemberRoleOpt.isEmpty()) {

            MemberRoleId memberRoleId = new MemberRoleId();
            memberRoleId.setMemberId(member.getMemberId());
            memberRoleId.setRoleId(role.getRoleId());
            memberRoleId.setOrganizationId(membershipRequest.organizationId());

            MemberRole newMemberRole = MemberRole.builder().id(memberRoleId).member(member).role(role).build();

            memberRoleRepository.save(newMemberRole);
        } else {

            MemberRole existingMemberRole = existingMemberRoleOpt.get();

            memberRoleRepository.updateMemberRole(
                    existingMemberRole.getId().getMemberId(),
                    existingMemberRole.getId().getOrganizationId(),
                    role.getRoleId()
            );
        }

        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    public MembershipResponse createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest) {
        UUID memberId = jwtUtil.extractMemberIdInternally();
        return createMembershipByOrganizationIdAndMemberId(createMembershipRequest.organizationId(), memberId, createMembershipRequest.membershipTypeId());
    }

    public MembershipResponse denyMembershipRequest(UUID membershipId, @Valid MembershipRequest membershipRequest) {
        Membership membership = getMembershipById(membershipId);

        setMembershipStatusByStatusName(membership, membershipRequest.membershipType(), "Denied");

        return membershipMapper.fromMembership(membershipRepository.save(membership));

    }

    public List<UUID> getActiveOrganizationIdsByMemberId(UUID memberId) {
        return membershipRepository.findActiveOrganizationIdsByMemberId(memberId);
    }

    public Membership getMembership(UUID memberId, UUID organizationId) {
        return membershipRepository.findMembershipByMemberIdAndOrganizationId(memberId, organizationId);
    }

    private Membership getMembershipById(UUID membershipId) {
        if(membershipId != null) {
            Optional<Membership> membershipOpt = membershipRepository.findByMembershipId(membershipId);

            if(membershipOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Membership not found with ID: " + membershipId);
            }

            return membershipOpt.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Membership Id cannot be null");
    }

    public List<MembershipResponse> getMembershipsByMemberId(UUID memberId) {
        List<Membership> memberships = membershipRepository.findMembershipsByMemberId(memberId);
        return memberships.stream().map(membershipMapper::fromMembership).collect(Collectors.toList());
    }

    public MembershipResponse getMembershipByMemberIdAndOrganizationId(UUID memberId, UUID organizationId) {
        Membership membership = membershipRepository.findMembershipByMemberIdAndOrganizationId(memberId, organizationId);
        return membershipMapper.fromMembership(membership);
    }

    public Page<MembershipResponse> getMembershipsByOrganization(
            UUID organizationId,
            Integer pageNo,
            Integer pageSize,
            String sortField,
            String sortOrder,
            MembershipFilters membershipFilters) {

        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort sort = Sort.unsorted();

        // Check if the sortField is 'role.name' and set the appropriate sorting
        if ("role.name".equals(sortField)) {
            sort = Sort.by(Sort.Order.by("role.name").with(Sort.Direction.fromString(sortOrder)));
        } else {
            sort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));
        }

        // Fix for problem: Sorting secondary criteria (to prevent duplicates on pages)
        Sort secondarySort = Sort.by(Sort.Order.by(sortField.equals("member.firstName") ? "member.lastName" : "membershipId").with(Sort.Direction.fromString(sortOrder)));
        Sort combinedSort = sort.and(secondarySort);

        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        Specification<Membership> spec = Specification.where(MembershipSpecification.hasOrganizationIdAndMembershipTypeNotNull(organizationId));

        spec = spec.and(MembershipSpecification.filterByFirstName(membershipFilters.memberFirstName()));
        spec = spec.and(MembershipSpecification.filterByEmail(membershipFilters.memberEmail()));
        spec = spec.and(MembershipSpecification.filterByCity(membershipFilters.memberMemberAddressCity()));
        spec = spec.and(MembershipSpecification.filterByCountry(membershipFilters.memberMemberAddressCountry()));
        spec = spec.and(MembershipSpecification.filterByMembershipStatus(membershipFilters.membershipStatusNames()));
        spec = spec.and(MembershipSpecification.filterByMembershipTypes(membershipFilters.membershipTypeNames()));
        spec = spec.and(MembershipSpecification.filterByRoleNames(membershipFilters.roleNames(), organizationId));

        if (membershipFilters.startDates() != null && !membershipFilters.startDates().isEmpty()) {
            Date startDateFrom = membershipFilters.startDates().get(0);
            Date startDateTo = membershipFilters.startDates().get(1);
            spec = spec.and(MembershipSpecification.filterByStartDateRange(startDateFrom, startDateTo));
        }

        if (membershipFilters.endDates() != null && !membershipFilters.endDates().isEmpty()) {
            Date endDateFrom = membershipFilters.endDates().get(0);
            Date endDateTo = membershipFilters.endDates().get(1);
            spec = spec.and(MembershipSpecification.filterByEndDateRange(endDateFrom, endDateTo));
        }

        spec = spec.and(MembershipSpecification.applySorting(combinedSort, organizationId));

        Page<Membership> membershipPage = membershipRepository.findAll(spec, pageable);


        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membership -> {
                    System.out.println("Organization Id: " + organizationId + " Member Id: " + membership.getMember().getMemberId());
                    Role memberRole = memberRoleRepository.findRoleByMemberAndOrganization(membership.getMember().getMemberId(), organizationId);
                    return  membershipMapper.fromMembershipWithRole(membership, memberRole);
                })
                .collect(Collectors.toList());


        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
    }

    public Page<MembershipResponse> getPendingMembershipsByOrganization(
            UUID organizationId,
            Integer pageNo,
            Integer pageSize,
            String sortField,
            String sortOrder,
            MembershipFilters membershipFilters) {

        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort firstSort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));

        // Fix for problem: There are items that shows on different pages. Because of same ex. name.
        Sort secondSort = Sort.by(Sort.Order.by(sortField.equals("member.firstName") ? "member.lastName" : "membershipId").with(Sort.Direction.fromString(sortOrder)));

        Sort combinedSort = firstSort.and(secondSort);

        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        Specification<Membership> spec = Specification.where(MembershipSpecification.hasOrganizationId(organizationId));
        spec = spec.and(MembershipSpecification.hadMembershipTypePending());
        spec = spec.and(MembershipSpecification.filterByFirstName(membershipFilters.memberFirstName()));
        spec = spec.and(MembershipSpecification.filterByEmail(membershipFilters.memberEmail()));
        spec = spec.and(MembershipSpecification.filterByCity(membershipFilters.memberMemberAddressCity()));
        spec = spec.and(MembershipSpecification.filterByCountry(membershipFilters.memberMemberAddressCountry()));

        Page<Membership> membershipPage = membershipRepository.findAll(spec, pageable);

        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membershipMapper::fromMembership)
                .collect(Collectors.toList());

        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
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

    private MembershipResponse createMembership(@Valid MembershipRequest membershipRequest) {
        Membership membership = membershipMapper.toMembership(membershipRequest);
        return membershipMapper.fromMembership(membershipRepository.save(membership));
    }

    private MembershipResponse createMembershipByOrganizationIdAndMemberId(UUID organizationId, UUID memberId, UUID membershipTypeId) {
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

    private void setMembershipStatusByStatusName(Membership membership, MembershipType membershipType, String membershipStatusName) {

        if(membershipStatusName != null) {
            MembershipStatus membershipStatus = membershipStatusRepository.findByName(membershipStatusName);

            membership.setMembershipStatus(membershipStatus);
            if(!membershipStatusName.equals("Denied")) {
                membership.setStartDate(Timestamp.from(Instant.now()));
                membership.setEndDate(getEndDate(membershipType));
            }
        } else {
            membership.setMembershipStatus(null);
        }
    }

}
