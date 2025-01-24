package com.rjproj.memberapp.member.repository;

import com.rjproj.memberapp.member.model.Membership;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MembershipRepository extends MongoRepository<Membership, UUID> {
}
