package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.GetMenuResponse;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.repository.RoleRepository;
import com.rjproj.memberapp.security.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    @Autowired
    private RoleRepository roleRepository;

    public GetMenuResponse getMenu() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID memberId = ((MemberDetails)principal).getMember().getMemberId();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
        } else {
            String username = principal.toString();
        }

       return null;
    }
}
