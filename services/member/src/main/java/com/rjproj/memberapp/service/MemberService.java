package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.mapper.MemberMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtEncoder jwtEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final MemberMapper memberMapper;

    public MemberResponse createMember(@Valid MemberRequest memberRequest) {
        Member member = memberMapper.toMember(memberRequest);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberMapper.fromMember(memberRepository.save(member));
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(memberMapper::fromMember)
                .collect(Collectors.toList());
    }

    public MemberResponse findById(UUID memberId) {
        return memberRepository.findById(memberId)
                .map(memberMapper::fromMember)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID:: " + memberId));
    }

    public MemberResponse updateMember(UUID memberId, @Valid MemberRequest memberRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update member with id %s", memberId.toString())
                ));
        mergeMember(member, memberRequest);
        return memberMapper.fromMember(memberRepository.save(member));
    }


    public void deleteMember(UUID memberId) {
        memberRepository.deleteById(memberId);
    }


    private void mergeMember(Member member, @Valid MemberRequest memberRequest) {
        if(StringUtils.isNotBlank(memberRequest.firstName())) {
            member.setFirstName(memberRequest.firstName());
        }
        if(StringUtils.isNotBlank(memberRequest.lastName())) {
            member.setLastName(memberRequest.lastName());
        }
        if(StringUtils.isNotBlank(memberRequest.email())) {
            member.setEmail(memberRequest.email());
        }
        if(memberRequest.memberAddress() != null) {
            member.setMemberAddress(memberRequest.memberAddress());
        }
    }

    public MemberResponse registerMember(MemberRequest memberRequest){
        Optional<Member> retrievedMember = memberRepository.findByEmail(memberRequest.email());
        Member member = memberMapper.toMember(memberRequest);
        if (retrievedMember.isPresent()){
            //throw
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        return memberMapper.fromMember(memberRepository.save(member));
    }

    public Map<String, Object> login(String username, String password) {
        Optional<Member> userEntity = memberRepository.findByEmail(username);
        Map<String, Object> response = new HashMap<>();
        if (!userEntity.isPresent()){
            response.put("status", "Member Not Found");
            return response;
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if(authentication.isAuthenticated()) {
            String accessToken = generateToken(userEntity.get(), authentication, 3600);
            response.put("access_token", accessToken);
            response.put("expires_in", 3600);
            return response;
        } else {
             throw new RuntimeException("Authentication failed");
        }
    }

    private String generateToken(Member member, Authentication authentication, long expiryDuration){
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("Ornate")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiryDuration))
                .subject(authentication.getName())
                .claim("role", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .claim("firstName", member.getFirstName())
                .claim("lastName", member.getLastName())
                .claim("userId", member.getMemberId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    }
}
