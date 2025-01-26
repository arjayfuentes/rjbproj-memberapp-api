package com.rjproj.memberapp.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {


    USER_READ_ALL("com.rjproj.memberapp.permission.user.readAll"),
    USER_READ_OWN("com.rjproj.memberapp.permission.user.readOwn"),
    USER_UPDATE_ALL("com.rjproj.memberapp.permission.user.updateAll"),
    USER_UPDATE_OWN("com.rjproj.memberapp.permission.user.updateOwn"),
    USER_CREATE("com.rjproj.memberapp.permission.user.create"),
    USER_DELETE("com.rjproj.memberapp.permission.user.delete"),

    //ORG_READ_ALL("com.rjproj.memberapp.permission.user.readAll"),
    ;

    @Getter
    private final String permission;
}