package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.organization.Organization;
import com.rjproj.memberapp.organization.OrganizationResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class OrganizationMapper {

    public Organization toOrganization(@Valid OrganizationResponse organizationResponse) {
        return Organization.builder()
                .organizationId(organizationResponse.organizationId())
                .name(organizationResponse.name())
                .description(organizationResponse.description())
                .organizationAddress(organizationResponse.organizationAddress())
                .build();
    }
}
