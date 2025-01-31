package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.mapper.MemberMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Permission;
import com.rjproj.memberapp.model.Role;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.repository.MemberRoleRepository;
import com.rjproj.memberapp.repository.PermissionRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.exception.MemberErrorMessage.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

//    @Autowired
//    JwtEncoder jwtEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    JWTUtil jwtUtil;

    private final MemberMapper memberMapper;

    public MemberResponse createMember(@Valid MemberRequest memberRequest) {
        return addMember(memberRequest);
    }

    public List<MemberResponse> findAll() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        UUID memberId = ((MemberDetails)principal).getMember().getMemberId();
//        if (principal instanceof UserDetails) {
//            String username = ((UserDetails)principal).getUsername();
//        } else {
//            String username = principal.toString();
//        }
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

            List<UUID> organizationIdsOfMember = membershipService.getOrganizationIdsByMemberId(member.get().getMemberId());

            List<String> preLogInPermissions = new ArrayList<>();

            UUID activeOrganizationId;
            Role activeRole = null;

            if(organizationIdsOfMember.size() == 1) {
                activeOrganizationId = organizationIdsOfMember.getFirst();
                activeRole = memberRoleRepository.findRolesByMemberAndOrganization(member.get().getMemberId(), activeOrganizationId);
                preLogInPermissions = activeRole.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList());
            } else {
                preLogInPermissions.add("com.rjproj.memberapp.permission.organization.viewOwn");
            }



            String jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions);
            MemberResponse memberResponse = memberMapper.fromMember(member.get());




            LoginResponse loginResponse = new LoginResponse(
                    jwt,
                    "Bearer",
                    memberResponse,
                    preLogInPermissions,
                    organizationIdsOfMember.size() == 1 ? organizationIdsOfMember.get(0) : null,
                    organizationIdsOfMember.size() == 0 ? null : organizationIdsOfMember
            );
            return ResponseHandler.generateResponse("User logged in successfully", HttpStatus.OK, loginResponse);
        }
        catch (BadCredentialsException e)
        {
            throw new MemberException("Incorrect password for " + loginRequest.email(), PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }





    public  ResponseEntity<Object>  selectLoginOrganization(@Valid SelectOrganizationLoginRequest selectOrganizationRequest) {

        try {
            Role activeRole = memberRoleRepository.findRolesByMemberAndOrganization(selectOrganizationRequest.memberId(), selectOrganizationRequest.organizationId());

            Member member = memberRepository.findById(selectOrganizationRequest.memberId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Cannot update member with id %s", selectOrganizationRequest.memberId())
                    ));

            List<String> activeAuthorities = Optional.ofNullable(activeRole)
                    .map(role -> role.getPermissions().stream().map(p -> p.getName()).toList())
                    .orElse(Collections.emptyList());

            //update token again
            String jwt = jwtUtil.generateToken(member.getEmail(), activeRole, activeAuthorities);

            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            // Extract the current user details
            MemberDetails currentUser = (MemberDetails) currentAuth.getPrincipal();
            currentUser.setActiveRole(activeRole);

            // Extract the current user details
            List<SimpleGrantedAuthority> updatedAuthorities = activeAuthorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

            // Create a new Authentication object with the updated authorities
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    currentUser, currentUser.getPassword(), updatedAuthorities);

            // Update the SecurityContext with the new authentication object
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            List<UUID> organizationIdsOfMember = membershipService.getOrganizationIdsByMemberId(member.getMemberId());

            LoginResponse loginResponse = new LoginResponse(
                    jwt,
                    "Bearer",
                    memberMapper.fromMember(member),
                    activeAuthorities,
                    selectOrganizationRequest.organizationId(),
                    organizationIdsOfMember
            );

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            return ResponseHandler.generateResponse("User logged in successfully", HttpStatus.OK, loginResponse);
        }
        catch (BadCredentialsException e)
        {
            throw new MemberException("Incorrect password for " + selectOrganizationRequest.memberId(), PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}