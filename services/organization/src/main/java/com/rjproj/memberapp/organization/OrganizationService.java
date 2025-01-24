package com.rjproj.memberapp.organization;

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
public class OrganizationService {


    private final OrganizationRepository organizationRepository;

    private final OrganizationMapper organizationMapper;

    public OrganizationResponse createOrganization(@Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationMapper.toOrganization(organizationRequest);
        return organizationMapper.fromOrganization(organizationRepository.save(organization));
    }

    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::fromOrganization)
                .collect(Collectors.toList());
    }

    public OrganizationResponse findById(UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .map(organizationMapper::fromOrganization)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID:: " + organizationId));
    }

    public OrganizationResponse updateOrganization(@Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.findById(organizationRequest.organizationId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update member with id %s", organizationRequest.organizationId())
                ));
        mergeOrganization(organization, organizationRequest);
        return organizationMapper.fromOrganization(organizationRepository.save(organization));
    }


    private void mergeOrganization(Organization organization, @Valid OrganizationRequest organizationRequest) {
        if(StringUtils.isNotBlank(organizationRequest.name())) {
            organization.setName(organizationRequest.name());
        }
        if(StringUtils.isNotBlank(organizationRequest.description())) {
            organization.setDescription(organizationRequest.description());
        }
        if(organizationRequest.organizationAddress() != null) {
            organization.setOrganizationAddress(organizationRequest.organizationAddress());
        }
    }
}
