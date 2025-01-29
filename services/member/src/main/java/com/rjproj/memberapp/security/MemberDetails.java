package com.rjproj.memberapp.security;

import com.rjproj.memberapp.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;


public class MemberDetails implements UserDetails {

    private Member member;

    public MemberDetails(Member member) {
//        super(getAuthorities(), member.getPassword(), true, true, true,
//                true);
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
