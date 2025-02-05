package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MembershipTypeValidityResponse;
import com.rjproj.memberapp.mapper.MembershipTypeValidityMapper;
import com.rjproj.memberapp.repository.MembershipTypeValidityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTypeValidityService {

    @Autowired
    private MembershipTypeValidityRepository membershipTypeValidityRepository;


    private final MembershipTypeValidityMapper membershipTypeValidityMapper;


    public List<MembershipTypeValidityResponse> findAll() {
        return membershipTypeValidityRepository.findAll()
                .stream()
                .map(membershipTypeValidityMapper::fromMembershipTypeValidity)
                .collect(Collectors.toList());
    }
}
