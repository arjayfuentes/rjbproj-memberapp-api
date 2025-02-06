package com.rjproj.memberapp.membershiptype;

import com.rjproj.memberapp.dto.CreateMembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "membership-service",
        url = "${application.config.membership-url}"
)
public interface MembershipClient {

    @PostMapping("/createMembershipForCurrentMember")
    Optional<MembershipResponse> createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest);

}
