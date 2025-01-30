package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    @Query("SELECT m.organizationId FROM Membership m WHERE m.member.memberId = :memberId")
    List<UUID> findOrganizationIdsByMemberId(@Param("memberId") UUID memberId);


}
