package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrganizationRepository extends MongoRepository<Organization, UUID> {
}
