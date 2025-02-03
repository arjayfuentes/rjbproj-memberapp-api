package com.rjproj.memberapp.mapper;

import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper {


    public RoleResponse fromRole(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getName()
        );
    }
}
