package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.OrganizationAddressIsRequiredException;
import com.rjproj.memberapp.exception.OrganizationNotFoundException;
import com.rjproj.memberapp.mapper.OrganizationMapper;
import com.rjproj.memberapp.membershiptype.MemberClient;
import com.rjproj.memberapp.membershiptype.MembershipClient;
import com.rjproj.memberapp.membershiptype.MembershipTypeClient;
import com.rjproj.memberapp.model.ImageMetadata;
import com.rjproj.memberapp.model.Organization;
import com.rjproj.memberapp.repository.ImageMetadataRepository;
import com.rjproj.memberapp.repository.OrganizationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import com.rjproj.memberapp.model.ImageMetadata;
import com.rjproj.memberapp.repository.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final OrganizationMapper organizationMapper;

    private final ImageMetadataRepository imageMetadataRepository;

    private static final String UPLOAD_DIR = "uploads/";

    private final MongoTemplate mongoTemplate;

    private final MemberClient memberClient;

    private final MembershipClient membershipClient;

    private final MembershipTypeClient membershipTypeClient;



    @Autowired
    private FileService fileService;

    public String addOrganization(@Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.save(organizationMapper.toOrganization(organizationRequest));
        return organization.getOrganizationId();
    }

    public OrganizationResponse updateOrganization(@Valid OrganizationRequest organizationRequest) {
        Organization organization = organizationRepository.findById(organizationRequest.organizationId())
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("Cannot update member with id %s", organizationRequest.organizationId())
                ));
        mergeOrganization(organization, organizationRequest);
        Organization updatedOrganization = organizationRepository.save(organization);
        return organizationMapper.fromOrganization(updatedOrganization);
    }

    public List<OrganizationResponse> saveOrganizations(List<OrganizationRequest> organizationRequests) {
        List<Organization> organizations = new ArrayList<>();
        organizationRequests.forEach(organizationRequest -> {
            organizations.add(organizationRepository.save(organizationMapper.toOrganization(organizationRequest)));
        });
        List<Organization> savedOrganizations = organizationRepository.saveAll(organizations);
        return savedOrganizations.stream().map(organizationMapper::fromOrganization).collect(Collectors.toList());
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

    public List<OrganizationResponse> findAllOrganization() {
        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::fromOrganization)
                .collect(Collectors.toList());
    }

    public List<OrganizationResponse> findMyOrganization(UUID memberId) {
        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::fromOrganization)
                .collect(Collectors.toList());
    }

    public OrganizationResponse findById(String organizationId) {
        return organizationRepository.findById(organizationId)
                .map(organizationMapper::fromOrganization)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        String.format("No organizaiton found with the provided ID: %s", organizationId))
                );
    }

    public Boolean existsById(String memberId) {
        return organizationRepository.findById(memberId)
                .isPresent();
    }

    public void deleteOrganization(String memberId) {
        organizationRepository.deleteById(memberId);
    }

    public List<OrganizationResponse> findOrganizationsByIds(List<String> organizationIds) {
        try {
            return organizationRepository.findByOrganizationIdIn(organizationIds)
                    .stream()
                    .map(organizationMapper::fromOrganization)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public OrganizationResponse completeCreateOrganization(MultipartFile logoImage, MultipartFile backgroundImage, @Valid CreateOrganizationRequest createOrganizationRequest) {

        com.rjproj.memberapp.model.File savedLogoImage = fileService.saveFile(logoImage, logoImage.getName());
        com.rjproj.memberapp.model.File savedBackgroundImage = fileService.saveFile(backgroundImage, backgroundImage.getName());

        Organization organization = organizationMapper.toOrganization(createOrganizationRequest.organizationRequest());
        organization.setLogoUrl(savedLogoImage.getFileUrl());
        organization.setBackgroundImageUrl(savedBackgroundImage.getFileUrl());

        Organization savedOrganization = organizationRepository.save(organization);

        List<MembershipTypeRequest> updatedMembershipTypeRequests = createOrganizationRequest.membershipTypes().stream()
                .map(membershipTypeRequest -> {
                    return new MembershipTypeRequest(
                            membershipTypeRequest.membershipTypeId(),
                            savedOrganization.getOrganizationId().toString(),
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

        String memberId = memberClient.createDefaultAdminOrganizationRoleForOwner(UUID.fromString(savedOrganization.getOrganizationId())).get();
        return organizationMapper.fromOrganization(savedOrganization);

    }
}
