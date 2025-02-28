package com.rjproj.memberapp.membershiptype;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;

@FeignClient(
        name = "member-service",
        url = "${application.config.member-url}"
)
public interface MemberClient {

    @PostMapping("/createDefaultAdminOrganizationRoleForOwner")
    Optional<String> createDefaultAdminOrganizationRoleForOwner(@RequestBody @Valid UUID organizationId);
}
