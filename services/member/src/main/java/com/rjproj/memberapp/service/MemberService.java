package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.mapper.MemberMapper;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.mapper.OrganizationMapper;
import com.rjproj.memberapp.mapper.RoleMapper;
import com.rjproj.memberapp.model.Member;
import com.rjproj.memberapp.model.Role;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.repository.MemberRepository;
import com.rjproj.memberapp.repository.MemberRoleRepository;
import com.rjproj.memberapp.repository.PermissionRepository;
import com.rjproj.memberapp.security.JWTUtil;
import com.rjproj.memberapp.security.MemberDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private final OrganizationClient organizationClient;

    private final MemberMapper memberMapper;

    private final MembershipMapper membershipMapper;

    private final RoleMapper roleMapper;

    private final OrganizationMapper organizationMapper;

    @Autowired
    private ModelMapper modelMapper;

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


    public Session login(LoginRequest loginRequest) {

        try {
            Optional<Member> member = memberRepository.findByEmail(loginRequest.email());

            if(!member.isPresent()) {
                throw new MemberException("Member with email address " + loginRequest.email() + " does not exists", MEMBER_NOT_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
            }

            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequest.email());

            MemberResponse memberResponse = memberMapper.fromMember(member.get());
            Role activeRole = null;
            RoleResponse roleResponse = null;
            List<String> preLogInPermissions = new ArrayList<>();
            OrganizationResponse activeOrganization = null;
            List<UUID> organizationIdsOfMember = membershipService.getOrganizationIdsByMemberId(member.get().getMemberId());
            MembershipResponse activeMembership = null;

            UUID activeOrganizationId;

            if(organizationIdsOfMember.size() == 1) {
                activeOrganizationId = organizationIdsOfMember.getFirst();
                activeOrganization = this.organizationClient.findOrganizationById(activeOrganizationId);

                activeRole = memberRoleRepository.findRolesByMemberAndOrganization(member.get().getMemberId(), activeOrganizationId);
                roleResponse = roleMapper.fromRole(activeRole);
                preLogInPermissions = activeRole.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList());
                activeMembership =  membershipMapper.fromMembership(membershipService.getMembershipByMemberIdAndOrganizationId(member.get().getMemberId(), activeOrganizationId));
            } else {
                preLogInPermissions.add("com.rjproj.memberapp.permission.organization.viewOwn");
            }


            String jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions);

            return new Session(
                    jwt,
                    "Bearer",
                    memberResponse,
                    roleResponse,
                    preLogInPermissions,
                    activeOrganization,
                    organizationIdsOfMember.size() == 0 ? null : organizationIdsOfMember,
                    activeMembership
            );
        }
        catch (BadCredentialsException e)
        {
            throw new MemberException("Incorrect password for " + loginRequest.email(), PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }





    public Session selectLoginOrganization(@Valid SelectOrganizationLoginRequest selectOrganizationRequest) {

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

            MemberResponse memberResponse = memberMapper.fromMember(member);
            RoleResponse roleResponse  = roleMapper.fromRole(activeRole);
            OrganizationResponse organizationResponse = this.organizationClient.findOrganizationById(selectOrganizationRequest.organizationId());
            List<UUID> organizationIdsOfMember = membershipService.getOrganizationIdsByMemberId(member.getMemberId());
            MembershipResponse membershipResponse =  membershipMapper.fromMembership(membershipService.getMembershipByMemberIdAndOrganizationId(member.getMemberId(), selectOrganizationRequest.organizationId()));

            return new Session(
                    jwt,
                    "Bearer",
                    memberResponse,
                    roleResponse,
                    activeAuthorities,
                    organizationResponse,
                    organizationIdsOfMember,
                    membershipResponse
            );
        }
        catch (BadCredentialsException e)
        {
            throw new MemberException("Incorrect password for " + selectOrganizationRequest.memberId(), PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}