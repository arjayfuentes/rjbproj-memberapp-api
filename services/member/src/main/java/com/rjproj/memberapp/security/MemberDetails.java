package com.rjproj.memberapp.security;

import com.rjproj.memberapp.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDetails extends User {


    private Member member;

    public MemberDetails(Member member) {
        super(member.getEmail(), member.getPassword(), true, true, true,
                true, getAuthorities(member));
        this.member = member;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        return member.getPermissionNames().stream().map(p -> new SimpleGrantedAuthority(p)).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.member.getPassword();
    }

    @Override
    public String getUsername() {
        return this.member.getEmail();
    }

    public Member getMember() {
        return this.member;
    }
}
