package com.rjproj.memberapp.dto;

public record GoogleTokenInfo (
        String aud,
        String email,
        String name
){
}
