package com.rjproj.memberapp.organization;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(
        name = "organization-service",
        url = "${application.config.organization-url}"
)
public interface OrganizationClient {

    @GetMapping("/findOrganizationById/{organization-id}")
    Optional<OrganizationResponse> findOrganizationById(@PathVariable("organization-id") UUID organizationId);

}