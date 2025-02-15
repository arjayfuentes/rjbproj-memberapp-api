package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrganizationRepositoryCustomImpl implements OrganizationRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Organization> findByFilters(String name, String countryName, String cityName, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name, "i");
        }
        if (countryName != null && !countryName.isEmpty()) {
            criteria.and("organizationAddress.country").regex(countryName, "i");
        }
        if (cityName != null && !cityName.isEmpty()) {
            criteria.and("organizationAddress.city").regex(cityName, "i");
        }

        Query query = new Query(criteria).with(pageable);

        List<Organization> organizations = mongoTemplate.find(query, Organization.class);

        long count = mongoTemplate.count(query, Organization.class);

        return new PageImpl<>(organizations, pageable, count);
    }
}
