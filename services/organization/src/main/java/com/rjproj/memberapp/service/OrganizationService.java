package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.exception.OrganizationNotFoundException;
import com.rjproj.memberapp.mapper.OrganizationMapper;
import com.rjproj.memberapp.model.Organization;
import com.rjproj.memberapp.repository.OrganizationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;


    public UUID addOrganization(@Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.save(organizationMapper.toOrganization(organizationRequest));
        return organization.getOrganizationId();
    }

    public void updateOrganization(@Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.findById(organizationRequest.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("Cannot update member with id %s", organizationRequest.organizationId())
                ));
        mergeOrganization(organization, organizationRequest);
        organizationRepository.save(organization);
    }

    private void mergeOrganization(Organization member, @Valid OrganizationRequest organizationRequest) {
        if(StringUtils.isNotBlank(organizationRequest.name())) {
            member.setName(organizationRequest.name());
        }
        if(StringUtils.isNotBlank(organizationRequest.description())) {
            member.setDescription(organizationRequest.description());
        }
        if(organizationRequest.organizationAddress() != null) {
            member.setOrganizationAddress(organizationRequest.organizationAddress());
        }
    }

    public List<OrganizationResponse> findAllOrganization() {
        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::fromOrganization)
                .collect(Collectors.toList());
    }

    public OrganizationResponse findById(UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .map(organizationMapper::fromOrganization)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("No organizaiton found with the provided ID: %s", organizationId))
                );
    }

    public Boolean existsById(UUID memberId) {
        return organizationRepository.findById(memberId)
                .isPresent();
    }

    public void deleteOrganization(UUID memberId) {
        organizationRepository.deleteById(memberId);
    }

}
