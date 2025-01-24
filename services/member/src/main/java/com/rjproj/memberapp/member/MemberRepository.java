package com.rjproj.memberapp.member;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MemberRepository extends MongoRepository<Member, UUID> {
}
