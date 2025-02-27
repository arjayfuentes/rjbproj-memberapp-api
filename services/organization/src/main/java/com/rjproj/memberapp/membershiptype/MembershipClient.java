package com.rjproj.memberapp.membershiptype;

import com.rjproj.memberapp.dto.CreateMembershipRequest;
import com.rjproj.memberapp.dto.MembershipResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FeignClient(
        name = "membership-service",
        url = "${application.config.membership-url}"
)
public interface MembershipClient {

    @PostMapping("/current")
    Optional<MembershipResponse> createMembershipForCurrentMember(@RequestBody @Valid CreateMembershipRequest createMembershipRequest);

    @GetMapping("/members/{memberId}")
    Optional<List<MembershipResponse>> getMembershipsByMemberId(@PathVariable("memberId") UUID memberId);


}
