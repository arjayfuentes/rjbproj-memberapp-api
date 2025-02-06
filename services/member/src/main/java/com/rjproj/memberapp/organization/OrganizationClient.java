package com.rjproj.memberapp.organization;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "organization-service",
        url = "${application.config.organization-url}"
)
public interface OrganizationClient {

    @PostMapping({"/findOrganizationsByIds"})
    Optional<List<OrganizationResponse>> findOrganizationsByIds(@RequestBody @Valid List<UUID> organizationIds);

    @GetMapping("/findOrganizationById/{organization-id}")
    OrganizationResponse findOrganizationById(@PathVariable("organization-id") UUID organizationId);

    @GetMapping("/findMyOrganizationById/{organization-id}")
    OrganizationResponse findMyOrganizationById(@PathVariable("organization-id") UUID organizationId);
}