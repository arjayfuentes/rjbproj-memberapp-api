package com.rjproj.memberapp.membershiptype;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.UUID;

@FeignClient(
        name = "role-service",
        url = "${application.config.role-url}"
)
public interface RoleClient {

    @PostMapping("/organizations/{organizationId}/admin")
    Optional<String> createAdminRoleForOrganizationOwner(
            @PathVariable("organizationId") UUID organizationId
    );
}
