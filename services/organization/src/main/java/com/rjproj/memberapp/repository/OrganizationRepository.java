package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends MongoRepository<Organization, String> {

    List<Organization> findByOrganizationIdIn(List<String> ids);

    // Fetch organizations with pagination
    Page<Organization> findAll(Pageable pageable);
}
