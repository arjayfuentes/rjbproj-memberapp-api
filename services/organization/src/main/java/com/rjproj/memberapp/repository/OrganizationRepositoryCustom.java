package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationRepositoryCustom {
    Page<Organization> findByFilters(String name, String countryName, String cityName, Pageable pageable);
}
