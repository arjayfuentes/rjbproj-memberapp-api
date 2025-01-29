package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.LoginRequest;
import com.rjproj.memberapp.dto.LoginResponse;
import com.rjproj.memberapp.dto.MemberRequest;
import com.rjproj.memberapp.dto.MemberResponse;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.mapper.MemberMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.security.JWTUtil;
import com.rjproj.memberapp.security.MemberDetails;
import com.rjproj.memberapp.util.ResponseHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.exception.MemberErrorMessage.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    AuthenticationManager authenticationManager;

//    @Autowired
//    JwtEncoder jwtEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    JWTUtil jwtUtil;

    private final MemberMapper memberMapper;

    public MemberResponse createMember(@Valid MemberRequest memberRequest) {
        return addMember(memberRequest);
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

    public MemberResponse registerMember(MemberRequest memberRequest) {
        return addMember(memberRequest);
    }

    public MemberResponse addMember(MemberRequest memberRequest) {
        Optional<Member> retrievedMember = memberRepository.findByEmail(memberRequest.email());
        Member member = memberMapper.toMember(memberRequest);
        if (retrievedMember.isPresent()){
            throw new MemberException("Member with email address " + memberRequest.email() + " already exists", MEMBER_EXISTS.getMessage(), HttpStatus.CONFLICT);
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        return memberMapper.fromMember(memberRepository.save(member));
    }


    public ResponseEntity<Object> login(LoginRequest loginRequest) {
        try {
            Optional<Member> userEntity = memberRepository.findByEmail(loginRequest.email());

            Optional<Member> member = memberRepository.findByEmail(loginRequest.email());
            if(!member.isPresent()) {
                throw new MemberException("Member with email address " + loginRequest.email() + " does not exists", MEMBER_NOT_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
            }
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequest.email());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            MemberResponse memberResponse = memberMapper.fromMember(member.get());
            LoginResponse loginResponse = new LoginResponse(
                    jwt,
                    "Bearer",
                    memberResponse
            );
            return ResponseHandler.generateResponse("User logged in successfully", HttpStatus.OK, loginResponse);
        }
        catch (BadCredentialsException e)
        {
            throw new MemberException("Incorrect password for " + loginRequest.email(), PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}