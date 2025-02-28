package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.OrganizationAddressResponse;
import com.rjproj.memberapp.dto.OrganizationRequest;
import com.rjproj.memberapp.dto.OrganizationResponse;
import com.rjproj.memberapp.model.Organization;
import com.rjproj.memberapp.model.OrganizationAddress;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrganizationMapper {

    public OrganizationResponse fromOrganization(Organization organization) {
        return new OrganizationResponse(
                organization.getOrganizationId(),
                organization.getName(),
                organization.getDescription(),
                organization.getLogoUrl(),
                organization.getBackgroundImageUrl(),
                organization.getEmail(),
                organization.getPhoneNumber(),
                organization.getWebsiteUrl(),
                fromOrganizationAddress(organization.getOrganizationAddress())
        );
    }

    public OrganizationAddressResponse fromOrganizationAddress(OrganizationAddress organizationAddress) {
        return new OrganizationAddressResponse(
                organizationAddress.getOrganizationAddressId(),
                organizationAddress.getStreet(),
                organizationAddress.getCity(),
                organizationAddress.getProvinceState(),
                organizationAddress.getRegion(),
                organizationAddress.getPostalCode(),
                organizationAddress.getCountry()
        );
    }

    public Organization toOrganization(OrganizationRequest request) {

        //set organizationId, OgranizationAddressId and createdAt since UUID is not supported in mongodb and changing id manually return null for createdAt
        String organizationId = request.organizationId() != null ? request.organizationId() : UUID.randomUUID().toString();
        String organizationAddressId = request.organizationAddress().getOrganizationAddressId() != null ? request.organizationAddress().getOrganizationAddressId() : UUID.randomUUID().toString();
        Instant organizationAddressCreatedAt = request.organizationAddress().getCreatedAt() != null ? request.organizationAddress().getCreatedAt() : Instant.now();

        Organization mappedOrganization =  Organization.builder()
                .organizationId(organizationId)
                .name(request.name())
                .description(request.description())
                .logoUrl(request.logoUrl())
                .backgroundImageUrl(request.backgroundImageUrl())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .websiteUrl(request.websiteUrl())
                .organizationAddress(request.organizationAddress())
                .createdAt(Instant.now())
                .build();
        mappedOrganization.getOrganizationAddress().setOrganizationAddressId(organizationAddressId);
        mappedOrganization.getOrganizationAddress().setCreatedAt(organizationAddressCreatedAt);
        return mappedOrganization;
    }

}

