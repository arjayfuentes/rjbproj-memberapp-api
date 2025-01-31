package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.MemberRole;
import com.rjproj.memberapp.model.MemberRoleId;
import com.rjproj.memberapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MemberRoleRepository extends JpaRepository<MemberRole, MemberRoleId> {

    @Query("SELECT mr.role FROM MemberRole mr WHERE mr.id.memberId = :memberId AND mr.id.organizationId = :organizationId")
    Role findRolesByMemberAndOrganization(@Param("memberId") UUID memberId, @Param("organizationId") UUID organizationId);

}
