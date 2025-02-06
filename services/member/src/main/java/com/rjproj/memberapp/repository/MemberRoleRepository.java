package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.MemberRoleId;
import com.rjproj.memberapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRoleRepository extends JpaRepository<MemberRole, MemberRoleId> {

    @Query("SELECT mr.role FROM MemberRole mr WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId")
    Role findRolesByMemberAndOrganization(@Param("memberId") UUID memberId, @Param("organizationId") UUID organizationId);


    // Query to find MemberRole based on MemberId, OrganizationId, and RoleId
    @Query("SELECT mr FROM MemberRole mr WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId AND mr.id.roleId = :roleId")
    Optional<MemberRole> findByMemberIdOrganizationIdAndRoleId(
            @Param("memberId") UUID memberId,
            @Param("organizationId") UUID organizationId,
            @Param("roleId") UUID roleId
    );
}
