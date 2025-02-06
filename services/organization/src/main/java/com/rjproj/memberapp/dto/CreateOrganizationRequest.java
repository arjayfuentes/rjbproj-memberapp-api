package com.rjproj.memberapp.dto;

import com.rjproj.memberapp.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateOrganizationRequest(
        OrganizationRequest organizationRequest,
        List<MembershipTypeRequest> membershipTypes
) {
}
