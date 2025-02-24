package com.rjproj.memberapp.repository;


import com.rjproj.memberapp.model.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.UUID;

public interface MembershipRepositoryCustom {
    Page<Membership> findMembershipsByFilters(UUID organizationId, Map<String, Object> filters, Pageable pageable);
}

