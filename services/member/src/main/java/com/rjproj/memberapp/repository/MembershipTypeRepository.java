package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipTypeRepository extends JpaRepository<MembershipType, UUID> {
}
