package com.rjproj.memberapp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.model.Permission.*;


@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    USER_READ_ALL,
                    USER_UPDATE_ALL,
                    USER_CREATE,
                    USER_DELETE
            )
    ),
    MEMBER(
            Set.of(
                    USER_READ_OWN,
                    USER_UPDATE_OWN
            )
    )

    ;

    @Getter
    private final Set<Permission> permissions;

//    public List<SimpleGrantedAuthority> getAuthorities() {
//        var authorities = getPermissions()
//                .stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toList());
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
//        return authorities;
//    }
}