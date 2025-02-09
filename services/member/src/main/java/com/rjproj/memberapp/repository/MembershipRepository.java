package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Membership;
import com.rjproj.memberapp.model.MembershipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    @Query("SELECT m.organizationId FROM Membership m WHERE m.member.memberId = :memberId")
    List<UUID> findOrganizationIdsByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT m.organizationId FROM Membership m WHERE m.member.memberId = :memberId AND m.membershipType IS NOT NULL")
    List<UUID> findActiveOrganizationIdsByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT m FROM Membership m WHERE m.member.memberId = :memberId AND m.organizationId = :organizationId")
    Membership findMembershipByMemberIdAndOrganizationId(@Param("memberId") UUID memberId, @Param("organizationId") UUID organizationId);

    List<Membership> findByOrganizationId(UUID organizationId);

    // Paginated query with a filter to only include memberships where membershipType is not null
    @Query("SELECT m FROM Membership m WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL")
    Page<Membership> findMembershipsByOrganizationId(UUID organizationId, Pageable pageable);


//    @Query("SELECT m FROM Membership m JOIN m.memberRoles r WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL ORDER BY r.role.name")
//    Page<Membership> findMembershipsByOrganizationIdSortedByRoleName(UUID organizationId, Pageable pageable);

    @Query("SELECT m FROM Membership m " +
            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
            "JOIN Role r ON mr.role.roleId = r.roleId " +
            "WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL " +
            "ORDER BY r.name ASC") // Default sorting ASC for role.name
    Page<Membership> findMembershipsByOrganizationIdSortedByRoleName(UUID organizationId, Pageable pageable);





//    @Query("SELECT m FROM Membership m " +
//            "JOIN MemberRole mr ON m.member.memberId = mr.member.memberId " +
//            "JOIN Role r ON mr.role.roleId = r.roleId " +
//            "WHERE m.organizationId = :organizationId AND m.membershipType IS NOT NULL")
//    Page<Membership> findMembershipsByOrganizationId(UUID organizationId, Pageable pageable);



    // Paginated query with a filter to only include memberships where membershipType is not null
    @Query("SELECT m FROM Membership m WHERE m.organizationId = :organizationId AND m.membershipType IS NULL")
    Page<Membership> findPendingMembershipsByOrganizationId(UUID organizationId, Pageable pageable);


}
