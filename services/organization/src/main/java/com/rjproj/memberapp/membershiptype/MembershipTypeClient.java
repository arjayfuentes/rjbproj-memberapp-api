package com.rjproj.memberapp.membershiptype;

import com.rjproj.memberapp.dto.MembershipTypeRequest;
import com.rjproj.memberapp.dto.MembershipTypeResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "membership-type-service",
        url = "${application.config.membership-type-url}"
)
public interface MembershipTypeClient {

    @PostMapping("/bulk")
    Optional<List<MembershipTypeResponse>> createMembershipTypes(@RequestBody @Valid List<MembershipTypeRequest> membershipTypeRequests);

}
