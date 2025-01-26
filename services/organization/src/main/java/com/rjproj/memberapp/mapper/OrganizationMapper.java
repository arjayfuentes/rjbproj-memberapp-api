package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.model.Organization;
import org.springframework.stereotype.Service;

@Service
public class OrganizationMapper {

    public Organization toOrganization(OrganizationRequest request) {
        return Organization.builder()
                .organizationId(request.organizationId())
                .name(request.name())
                .description(request.description())
                .organizationAddress(request.organizationAddress())
                .build();
    }


    public OrganizationResponse fromOrganization(Organization organization) {
        return new OrganizationResponse(
                organization.getOrganizationId(),
                organization.getName(),
                organization.getDescription(),
                organization.getCreatedAt(),
                organization.getUpdatedAt(),
                organization.getOrganizationAddress()
        );
    }

}

