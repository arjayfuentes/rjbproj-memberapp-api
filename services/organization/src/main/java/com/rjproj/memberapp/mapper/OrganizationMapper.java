package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.model.Organization;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationMapper {

    public Organization toOrganization(OrganizationRequest request) {
        String organizationAddressId = request.organizationAddress().getOrganizationAddressId() != null ? request.organizationAddress().getOrganizationAddressId() : UUID.randomUUID().toString();
        Organization mappedOrganization =  Organization.builder()
                .organizationId(request.organizationId())
                .name(request.name())
                .description(request.description())
                .organizationAddress(request.organizationAddress())
                .build();
        mappedOrganization.getOrganizationAddress().setOrganizationAddressId(organizationAddressId);
        return mappedOrganization;
    }


    public OrganizationResponse fromOrganization(Organization organization) {
        return new OrganizationResponse(
                organization.getOrganizationId(),
                organization.getName(),
                organization.getDescription(),
                organization.getOrganizationAddress(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }

}

