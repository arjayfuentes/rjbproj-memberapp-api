package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface OrganizationRepository extends MongoRepository<Organization, String> ,OrganizationRepositoryCustom {

    List<Organization> findByOrganizationIdIn(List<String> ids);

    Page<Organization> findAll(Pageable pageable);

    Page<Organization> findByNameContainingIgnoreCase(String name, Pageable pageable);


    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$organizationAddress.country' } }",
            "{ '$project': { 'country': '$_id', '_id': 0 } }"
    })
    List<Map<String, String>> findDistinctCountries();

    @Aggregation(pipeline = {
            "{ $match: { 'organizationAddress.country': ?0 } }",  // Match organizations by country
            "{ $group: { _id: '$organizationAddress.city' } }"   // Group by city to get distinct values
    })
    List<String> findDistinctCitiesByCountry(String country);

}
