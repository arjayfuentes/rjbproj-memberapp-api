package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.mapper.RoleMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Role;
import com.rjproj.memberapp.repository.MemberRoleRepository;
import com.rjproj.memberapp.repository.RoleRepository;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public List<RoleResponse> getMemberRoles() {
        List<String> userRoleNames = new ArrayList<>();
        userRoleNames.add("Member");
        userRoleNames.add("Admin");
       List<Role> roles = roleRepository.findByNameIn(userRoleNames);
       return roles.stream().map(roleMapper::fromRole).collect(Collectors.toList()) ;
    }
}
