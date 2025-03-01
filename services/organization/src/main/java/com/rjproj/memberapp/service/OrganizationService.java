package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.OrganizationNotFoundException;
import com.rjproj.memberapp.mapper.OrganizationMapper;
import com.rjproj.memberapp.membershiptype.MembershipClient;
import com.rjproj.memberapp.membershiptype.MembershipTypeClient;
import com.rjproj.memberapp.membershiptype.RoleClient;
import com.rjproj.memberapp.model.ImageType;
import com.rjproj.memberapp.model.Organization;
import com.rjproj.memberapp.repository.OrganizationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    @Autowired
    private FileService fileService;

    private final MembershipClient membershipClient;

    private final MembershipTypeClient membershipTypeClient;

    private final OrganizationMapper organizationMapper;

    private final OrganizationRepository organizationRepository;

    private final RoleClient roleClient;

    public OrganizationResponse createOrganization(MultipartFile logoImage, MultipartFile backgroundImage, @Valid CreateOrganizationRequest createOrganizationRequest) {

        try {
            Organization organization = organizationMapper.toOrganization(createOrganizationRequest.organizationRequest());
            Organization savedOrganization = organizationRepository.save(organization);


            com.rjproj.memberapp.model.File savedLogoImage = fileService.saveFile("organization", savedOrganization.getOrganizationId(), ImageType.PROFILE_IMAGE, logoImage.getName(), logoImage);
            com.rjproj.memberapp.model.File savedBackgroundImage = fileService.saveFile("organization", savedOrganization.getOrganizationId(), ImageType.BACKGROUND_IMAGE, logoImage.getName(), backgroundImage);

            savedOrganization.setLogoUrl(savedLogoImage.getFileUrl());
            savedOrganization.setBackgroundImageUrl(savedBackgroundImage.getFileUrl());


            Organization finalsavedOrganization = organizationRepository.save(savedOrganization);


            List<MembershipTypeRequest> updatedMembershipTypeRequests = createOrganizationRequest.membershipTypes().stream()
                    .map(membershipTypeRequest -> {
                        return new MembershipTypeRequest(
                                membershipTypeRequest.membershipTypeId(),
                                finalsavedOrganization.getOrganizationId().toString(),
                                membershipTypeRequest.membershipTypeValidity(),
                                membershipTypeRequest.name(),
                                membershipTypeRequest.description(),
                                membershipTypeRequest.isDefault()
                        );
                    })
                    .collect(Collectors.toList());

            List<MembershipTypeResponse> membershipTypeResponses = this.membershipTypeClient.createMembershipTypes(updatedMembershipTypeRequests).get();

            Optional<MembershipTypeResponse> defaultMembershipType = membershipTypeResponses.stream()
                    .filter(MembershipTypeResponse::isDefault) // Filter by isDefault == true
                    .findFirst();

            CreateMembershipRequest createMembershipRequest = new CreateMembershipRequest(
                    null,
                    savedOrganization.getOrganizationId(),
                    defaultMembershipType.get().membershipTypeId()
            );
            Optional<MembershipResponse> membershipResponse = this.membershipClient.createMembershipForCurrentMember(createMembershipRequest);
            String memberId = roleClient.createAdminRoleForOrganizationOwner(UUID.fromString(savedOrganization.getOrganizationId())).get();
            return organizationMapper.fromOrganization(savedOrganization);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create membership: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public OrganizationResponse getOrganizationById(String organizationId) {
        return organizationRepository.findById(organizationId)
                .map(organizationMapper::fromOrganization)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("No organizaiton found with the provided ID: %s", organizationId))
                );
    }

    public List<OrganizationResponse> getOrganizationByIds(List<String> organizationIds) {
        try {
            return organizationRepository.findByOrganizationIdIn(organizationIds)
                    .stream()
                    .map(organizationMapper::fromOrganization)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Page<OrganizationResponse> getOrganizations(int page, int size, String name, String countryName, String cityName) {
        PageRequest pageRequest = PageRequest.of(page, size);

        // Call the custom repository method
        Page<Organization> organizations = organizationRepository.findByFilters(name, countryName, cityName, pageRequest);

        return organizations.map(org -> organizationMapper.fromOrganization(org));
    }

    public List<OrganizationResponse> getOrganizationsByMemberId(UUID memberId) {
        Optional<List<MembershipResponse>> membershipResponsesOpt = this.membershipClient.getMembershipsByMemberId(memberId);
        if(membershipResponsesOpt.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> organizationIds = membershipResponsesOpt.get().stream().map(membershipResponse -> membershipResponse.organizationId().toString()).collect(Collectors.toList());
        List<Organization> organizations = organizationRepository.findAllById(organizationIds);
        return organizations.stream()
                .map(organizationMapper::fromOrganization)
                .sorted(Comparator.comparing(OrganizationResponse::name))
                .collect(Collectors.toList());
    }

    public List<String> getUniqueOrganizationCountries() {
        // Extracting the 'country' value from the returned map
        return organizationRepository.findDistinctCountries()
                .stream()
                .map(map -> map.get("country"))
                .collect(Collectors.toList());
    }

    public OrganizationResponse updateOrganization(String organizationId, @Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("Cannot update member with id %s", organizationId)
                ));
        mergeOrganization(organization, organizationRequest);
        Organization updatedOrganization = organizationRepository.save(organization);
        return organizationMapper.fromOrganization(updatedOrganization);
    }

    public OrganizationResponse updateOrganizationPhoto(String organizationId, MultipartFile image, String imageType) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization.isEmpty()) {
            throw new OrganizationNotFoundException("No organization found with the provided ID: " + organizationId);
        }


        ImageType type = imageType.equals(ImageType.BACKGROUND_IMAGE.getValue()) ? ImageType.BACKGROUND_IMAGE : ImageType.LOGO_IMAGE;
        com.rjproj.memberapp.model.File savedImage = fileService.saveFile("organization", organization.get().getOrganizationId(), type, image.getName(), image);
        if(savedImage.getFileUrl() != null) {
            if(imageType.equals(ImageType.LOGO_IMAGE.getValue())) {
                organization.get().setLogoUrl(savedImage.getFileUrl());
            }
            if(imageType.equals(ImageType.BACKGROUND_IMAGE.getValue())) {
                organization.get().setBackgroundImageUrl(savedImage.getFileUrl());
            }

            return organizationMapper.fromOrganization(organizationRepository.save(organization.get()));
        } else {
            throw new OrganizationNotFoundException("Error saving file");
        }

    }

    private void mergeOrganization(Organization member, @Valid OrganizationRequest organizationRequest) {
        if(StringUtils.isNotBlank(organizationRequest.name())) {
            member.setName(organizationRequest.name());
        }
        if(StringUtils.isNotBlank(organizationRequest.description())) {
            member.setDescription(organizationRequest.description());
        }
        if(StringUtils.isNotBlank(organizationRequest.logoUrl())) {
            member.setLogoUrl(organizationRequest.logoUrl());
        }
        if(StringUtils.isNotBlank(organizationRequest.backgroundImageUrl())) {
            member.setBackgroundImageUrl(organizationRequest.backgroundImageUrl());
        }
        if(StringUtils.isNotBlank(organizationRequest.email())) {
            member.setEmail(organizationRequest.email());
        }
        if(StringUtils.isNotBlank(organizationRequest.phoneNumber())) {
            member.setPhoneNumber(organizationRequest.phoneNumber());
        }
        if(StringUtils.isNotBlank(organizationRequest.websiteUrl())) {
            member.setWebsiteUrl(organizationRequest.websiteUrl());
        }
        if(organizationRequest.organizationAddress() != null) {
            member.setOrganizationAddress(organizationRequest.organizationAddress());
        }
        if(organizationRequest.organizationAddress().getOrganizationAddressId() == null) {
            member.getOrganizationAddress().setOrganizationAddressId(UUID.randomUUID().toString());
        }
    }

}
