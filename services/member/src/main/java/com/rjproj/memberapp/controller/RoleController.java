package com.rjproj.memberapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.dto.RoleResponse;
import com.rjproj.memberapp.service.MemberService;
import com.rjproj.memberapp.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/getMemberRoles")
    public ResponseEntity<List<RoleResponse>> getMemberRoles() {
        return ResponseEntity.ok(roleService.getMemberRoles());
    }
}
