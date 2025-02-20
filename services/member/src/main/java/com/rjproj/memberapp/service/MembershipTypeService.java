package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import com.rjproj.memberapp.mapper.MembershipTypeMapper;
import com.rjproj.memberapp.model.MembershipType;
import com.rjproj.memberapp.repository.MembershipTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTypeService {

    private final MembershipTypeRepository membershipTypeRepository;

    private final MembershipTypeMapper membershipTypeMapper;

    public MembershipTypeResponse createMembershipType(@Valid MembershipTypeRequest membershipTypeRequest) {
        MembershipType membershipType = membershipTypeMapper.toMembershipType(membershipTypeRequest);
        return membershipTypeMapper.fromMembershipType(membershipTypeRepository.save(membershipType));
    }

    public List<MembershipTypeResponse> findAll() {
        return membershipTypeRepository.findAll()
                .stream()
                .map(membershipTypeMapper::fromMembershipType)
                .collect(Collectors.toList());
    }

    public MembershipTypeResponse findById(UUID membershipTypeId) {
        return membershipTypeRepository.findById(membershipTypeId)
                .map(membershipTypeMapper::fromMembershipType)
                .orElseThrow(() -> new EntityNotFoundException("MembershipType not found with ID:: " + membershipTypeId));
    }


    public MembershipTypeResponse updateMembershipType(UUID membershipTypeId, @Valid MembershipTypeRequest membershipTypeRequest) {
        MembershipType membershipType = membershipTypeRepository.findById(membershipTypeId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update membershipType with id %s", membershipTypeId.toString())
                ));
        mergeMembershipType(membershipType, membershipTypeRequest);
        return membershipTypeMapper.fromMembershipType(membershipTypeRepository.save(membershipType));
    }


    public void deleteMembershipType(UUID membershipTypeId) {
        membershipTypeRepository.deleteById(membershipTypeId);
    }


    private void mergeMembershipType(MembershipType membershipType, @Valid MembershipTypeRequest membershipTypeRequest) {
        if(membershipTypeRequest.organizationId() != null) {
            membershipType.setOrganizationId(membershipTypeRequest.organizationId());
        }
        if(StringUtils.isNotBlank(membershipTypeRequest.name())) {
            membershipType.setName(membershipTypeRequest.name());
        }
        if(StringUtils.isNotBlank(membershipTypeRequest.description())) {
            membershipType.setDescription(membershipTypeRequest.description());
        }
    }

    public List<MembershipTypeResponse> createMembershipTypes(@Valid List<MembershipTypeRequest> membershipTypeRequests) {
        List<MembershipType> membershipTypes = membershipTypeRequests.stream().map(membershipTypeMapper::toMembershipType).collect(Collectors.toList());
        return membershipTypeRepository.saveAll(membershipTypes)
                .stream()
                .map(membershipTypeMapper::fromMembershipType)
                .collect(Collectors.toList());
    }

    public List<MembershipTypeResponse> getMembershipTypesByOrganizationId(UUID organizationId) {
        List<MembershipType> membershipTypes = membershipTypeRepository.findByOrganizationId(organizationId);
        return membershipTypes.stream().map(membershipTypeMapper::fromMembershipType).collect(Collectors.toList());
    }
}
