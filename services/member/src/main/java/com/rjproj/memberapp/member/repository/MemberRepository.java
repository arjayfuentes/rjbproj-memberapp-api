package com.rjproj.memberapp.member.repository;

import com.rjproj.memberapp.member.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MemberRepository extends MongoRepository<Member, UUID> {
}
