package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.mapper.MemberMapper;
import com.rjproj.memberapp.mapper.MembershipMapper;
import com.rjproj.memberapp.mapper.OrganizationMapper;
import com.rjproj.memberapp.mapper.RoleMapper;
import com.rjproj.memberapp.model.*;
import com.rjproj.memberapp.organization.OrganizationClient;
import com.rjproj.memberapp.organization.OrganizationResponse;
import com.rjproj.memberapp.repository.*;
import com.rjproj.memberapp.security.JWTUtil;
import com.rjproj.memberapp.security.MemberDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.exception.MemberErrorMessage.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private MembershipRepository membershipRepository;


    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private FileService fileService;

    @Autowired
    JWTUtil jwtUtil;

    private final OrganizationClient organizationClient;

    private final MemberMapper memberMapper;

    private final MembershipMapper membershipMapper;

    private final RoleMapper roleMapper;

    private final OrganizationMapper organizationMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    private final RestTemplate restTemplate;


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

    public MemberResponse addMember(MemberRequest memberRequest) {
        Optional<Member> retrievedMember = memberRepository.findByEmail(memberRequest.email());
        Member member = memberMapper.toMember(memberRequest);
        Optional<Role> defaultRole = roleRepository.findByName("Non-Member");


        if (retrievedMember.isPresent()){
            throw new MemberException("Member with email address " + memberRequest.email() + " already exists. Sign in to continue", MEMBER_EXISTS.getMessage(), HttpStatus.CONFLICT);
        }
        if(memberRequest.loginType() == LoginType.NORMAL) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }

        UUID defaultOrganizationId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Member savedMember = memberRepository.save(member);
        MemberRoleId memberRoleId = new MemberRoleId();
        memberRoleId.setMemberId(member.getMemberId());
        memberRoleId.setRoleId(defaultRole.get().getRoleId());
        memberRoleId.setOrganizationId(defaultOrganizationId);

        MemberRole memberRole = MemberRole.builder().id(memberRoleId).member(savedMember).role(defaultRole.get()).build();
        memberRoleRepository.save(memberRole);
        return memberMapper.fromMember(savedMember);
    }

    public MemberResponse createMember(@Valid MemberRequest memberRequest) {
        return addMember(memberRequest);
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
        if(StringUtils.isNotBlank(memberRequest.phoneNumber())) {
            member.setPhoneNumber(memberRequest.phoneNumber());
        }
        if(StringUtils.isNotBlank(memberRequest.profilePicUrl())) {
            member.setProfilePicUrl(memberRequest.profilePicUrl());
        }
        if(memberRequest.birthDate() != null) {
            member.setBirthDate(memberRequest.birthDate());
        }
        if(memberRequest.loginType() != null) {
            member.setLoginType(memberRequest.loginType());
        }
        if(memberRequest.memberAddress() != null) {
            member.setMemberAddress(memberRequest.memberAddress());
        }
    }

    public MemberResponse registerMember(MemberRequest memberRequest) {
        memberRequest = new MemberRequest(
                memberRequest.memberId(),
                memberRequest.firstName(),
                memberRequest.lastName(),
                memberRequest.email(),
                memberRequest.password(),
                memberRequest.phoneNumber(),
                memberRequest.profilePicUrl(),
                memberRequest.birthDate(),
                LoginType.NORMAL,  // Default value for loginType
                memberRequest.memberAddress()
        );
        return addMember(memberRequest);
    }

    public MemberResponse registerMemberWithGoogle(String googleCode) {
        GoogleInfo googleInfo = googleService.getGoogleInfo(googleCode);
        if(googleInfo == null) {
            throw new MemberException(
                    "Error signing up with google",
                    SIGN_UP_WITH_GOOGLE.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        MemberRequest memberRequest = new MemberRequest(
                null,
                googleInfo.firstName(),
                googleInfo.lastName(),
                googleInfo.email(),
                null,
                null,
                googleInfo.photoUrl(),
                googleInfo.birthdate(),
                LoginType.GOOGLE,
                null
        );
        return addMember(memberRequest);
    }

    public MemberResponse updateMemberAfterRegistration(MultipartFile profilePicImage,
                                              @Valid AdditionalInfoRequest additionalInfoRequest) {
        return updateMemberDetails(profilePicImage, additionalInfoRequest);
    }

    public MemberResponse updateMemberDetails(MultipartFile profilePicImage,
                                                        @Valid AdditionalInfoRequest additionalInfoRequest) {
        Optional<Member> member = memberRepository.findByEmail(additionalInfoRequest.memberRequest().email());

        if (member.isEmpty()) {
            throw new MemberException("The email address you provided does not exist.",
                    MEMBER_NOT_EXISTS.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        Member existingMember = member.get();
        String imageUrl = additionalInfoRequest.memberRequest().profilePicUrl(); // Default to existing URL

        if (profilePicImage != null && !profilePicImage.isEmpty()) {
            try {
                imageUrl = fileService.uploadImage("member",
                        existingMember.getMemberId(),
                        ImageType.PROFILE_IMAGE,
                        profilePicImage);
            } catch (IOException e) {
                throw new MemberException("Failed to upload profile image",
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        MemberRequest updatedRequest = new MemberRequest(
                additionalInfoRequest.memberRequest().memberId(),
                additionalInfoRequest.memberRequest().firstName(),
                additionalInfoRequest.memberRequest().lastName(),
                additionalInfoRequest.memberRequest().email(),
                additionalInfoRequest.memberRequest().password(),
                additionalInfoRequest.memberRequest().phoneNumber(),
                imageUrl,
                additionalInfoRequest.memberRequest().birthDate(),
                additionalInfoRequest.memberRequest().loginType(),
                additionalInfoRequest.memberRequest().memberAddress()
        );

        return this.updateMember(existingMember.getMemberId(), updatedRequest);
    }

    public Session login(LoginRequest loginRequest) {

        try {
            Optional<Member> member = memberRepository.findByEmail(loginRequest.email());

            if(!member.isPresent()) {
                throw new MemberException("The email address you provided does not exists.", MEMBER_NOT_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
            }

            if(member.get().getLoginType().equals(LoginType.GOOGLE)) {
                throw new MemberException("The email is associated with a google account. Sign in with your google account."
                        , SIGN_IN_WITH_GOOGLE.getMessage(), HttpStatus.BAD_REQUEST);
            }

            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            return getLoginMemberSession(member.get());
        }
        catch (BadCredentialsException e)
        {
            System.out.println(e);
            throw new MemberException("Incorrect password", PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Session loginMemberWithGoogle(String googleCode) {
        GoogleInfo googleInfo = googleService.getGoogleInfo(googleCode);
        Optional<Member> member = memberRepository.findByEmail(googleInfo.email());
        if(!member.isPresent()) {
            throw new MemberException(
                    "The google account you provided does not exists. Sign up first to contiue",
                    MEMBER_NOT_EXISTS.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        Member verifiedMember = member.get();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(verifiedMember.getEmail(), null, new ArrayList<>());
        return getLoginMemberSession(verifiedMember);

    }

    private Session getLoginMemberSession(Member member) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(member.getEmail());

        MemberResponse memberResponse = memberMapper.fromMember(member);
        Role activeRole = null;
        RoleResponse roleResponse = null;
        List<String> preLogInPermissions = new ArrayList<>();
        OrganizationResponse activeOrganization = null;
        List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.getMemberId());
        MembershipResponse activeMembership = null;

        UUID activeOrganizationId = null;

        String jwt = null;

        if(organizationIdsOfMember.size() == 1) {
            activeOrganizationId = organizationIdsOfMember.getFirst();

            activeRole = memberRoleRepository.findRoleByMemberAndOrganization(member.getMemberId(), activeOrganizationId);
            roleResponse = roleMapper.fromRole(activeRole);
            preLogInPermissions = activeRole.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList());
            activeMembership =  membershipMapper.fromMembership(membershipService.getMembership(member.getMemberId(), activeOrganizationId));
            jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions, activeOrganizationId, member.getMemberId());

            activeOrganization = this.organizationClient.findMyOrganizationById(activeOrganizationId);

        } else {
            preLogInPermissions.add("com.rjproj.memberapp.permission.organization.viewAll");
        }


        jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions, activeOrganizationId, member.getMemberId());

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

    public Session getLoginSession(String token) {
        if(jwtUtil.validateToken(token)) {

            UUID selectedOrganizationId = jwtUtil.extractSelectedOrganizationId(token);
            UUID memberId = jwtUtil.extractMemberId(token);

            Role activeRole = null;
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Cannot update member with id %s", memberId)
                    ));

            List<String> activeAuthorities = Optional.ofNullable(activeRole)
                    .map(role -> role.getPermissions().stream().map(p -> p.getName()).toList())
                    .orElse(Collections.emptyList());
            List<SimpleGrantedAuthority> updatedAuthorities = activeAuthorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if(selectedOrganizationId != null) {
                activeRole = memberRoleRepository.findRoleByMemberAndOrganization(memberId, selectedOrganizationId);
            }

            MemberDetails memberDetails = (MemberDetails) userDetailsServiceImpl.loadUserByUsername(member.getEmail());
            memberDetails.setActiveRole(activeRole);


            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberDetails, null, updatedAuthorities);

            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);




            MemberResponse memberResponse = memberMapper.fromMember(member);
            RoleResponse roleResponse = null;

            if(activeRole != null) {
                roleResponse = roleMapper.fromRole(activeRole);
            }


            OrganizationResponse organizationResponse = null;
            MembershipResponse membershipResponse = null;

            if(selectedOrganizationId != null) {
                organizationResponse = this.organizationClient.findMyOrganizationById(selectedOrganizationId);
                membershipResponse = membershipMapper.fromMembership(membershipService.getMembership(member.getMemberId(), selectedOrganizationId));
            }
            List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.getMemberId());


            return new Session(
                    token,
                    "Bearer",
                    memberResponse,
                    roleResponse,
                    activeAuthorities,
                    organizationResponse,
                    organizationIdsOfMember,
                    membershipResponse);
        } else {
            throw new MemberException("Please login again", UNAUTHORIZED.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public Session selectLoginOrganization(@Valid SelectOrganizationLoginRequest selectOrganizationRequest) {

        try {
            Role activeRole = memberRoleRepository.findRoleByMemberAndOrganization(selectOrganizationRequest.memberId(), selectOrganizationRequest.organizationId());
            Member member = memberRepository.findById(selectOrganizationRequest.memberId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Cannot update member with id %s", selectOrganizationRequest.memberId())
                    ));

            List<String> activeAuthorities = Optional.ofNullable(activeRole)
                    .map(role -> role.getPermissions().stream().map(p -> p.getName()).toList())
                    .orElse(Collections.emptyList());

            //update token again
            String jwt = jwtUtil.generateToken(member.getEmail(), activeRole, activeAuthorities, selectOrganizationRequest.organizationId(), member.getMemberId());

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
            RoleResponse roleResponse  = null;
            if(activeRole != null) {
                roleResponse = roleMapper.fromRole(activeRole);
            }
            OrganizationResponse organizationResponse = this.organizationClient.findMyOrganizationById(selectOrganizationRequest.organizationId());
            List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.getMemberId());
            MembershipResponse membershipResponse =  membershipMapper.fromMembership(membershipService.getMembership(member.getMemberId(), selectOrganizationRequest.organizationId()));

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

    public String createDefaultAdminOrganizationRoleForOwner(@Valid UUID organizationId) {

        UUID memberId = jwtUtil.extractMemberIdInternally();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update member with id %s", memberId)
                ));

        Role role = roleRepository.findByName("Admin").get();
        Optional<MemberRole> existingMemberRole = memberRoleRepository.findByMemberIdOrganizationIdAndRoleId(member.getMemberId(), organizationId, role.getRoleId());

        if (existingMemberRole.isPresent()) {
            throw new MemberException("Role already exists", MEMBER_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
        } else {

            MemberRole memberRole = new MemberRole();

            MemberRoleId memberRoleId = new MemberRoleId();
            memberRoleId.setMemberId(member.getMemberId());
            memberRoleId.setOrganizationId(organizationId);
            memberRoleId.setRoleId(role.getRoleId());

            memberRole.setId(memberRoleId);
            memberRole.setMember(member);
            memberRole.setRole(role);

            memberRoleRepository.save(memberRole);
            return member.getMemberId().toString();
        }

    }

    public List<MemberResponse> getMembersByOrganization(UUID organizationId) {
        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }
        List<Membership> memberships = membershipRepository.findByOrganizationId(organizationId);
        List<Member> members = memberships.stream().map(Membership::getMember).collect(Collectors.toList());
        return members
                .stream()
                .map(memberMapper::fromMember)
                .collect(Collectors.toList());
    }

    public Page<MemberResponse> getMembersByOrganizationPage(UUID organizationId, Integer pageNo, Integer pageSize) {
        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Membership> membershipPage = membershipRepository.findMembershipsByOrganizationId(organizationId, pageable);

        // Convert Membership -> Member -> MemberResponse
        List<MemberResponse> memberResponses = membershipPage.getContent().stream()
                .map(Membership::getMember)
                .map(memberMapper::fromMember)
                .collect(Collectors.toList());
        return new PageImpl<>(memberResponses, pageable, membershipPage.getTotalElements());
    }

    public Page<MemberResponse> getMembersByOrganizationPaginationAndSorting(
            UUID organizationId, Integer pageNo, Integer pageSize, String sortField, String sortOrder) {
        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort sort =  sortOrder.equals("ASC") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Membership> membershipPage = membershipRepository.findMembershipsByOrganizationId(organizationId, pageable);



        // Convert Membership -> Member -> MemberResponse
        List<MemberResponse> memberResponses = membershipPage.getContent().stream()
                .map(Membership::getMember)
                .map(memberMapper::fromMember)
                .collect(Collectors.toList());


        return new PageImpl<>(memberResponses, pageable, membershipPage.getTotalElements());
    }

    public Page<MembershipResponse> getMembershipsByOrganization(
            UUID organizationId,
            Integer pageNo,
            Integer pageSize,
            String sortField,
            String sortOrder,
            MembershipFilters membershipFilters) {

        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort sort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));
        Sort secondarySort = Sort.by(Sort.Order.by("membershipId").with(Sort.Direction.fromString(sortOrder)));
        Sort combinedSort = sort.and(secondarySort);

        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        Specification<Membership> spec = Specification.where(MembershipSpecification.hasOrganizationIdAndMembershipTypeNotNull(organizationId));

        spec = spec.and(MembershipSpecification.filterByFirstName(membershipFilters.memberFirstName()));
        spec = spec.and(MembershipSpecification.filterByEmail(membershipFilters.memberEmail()));
        spec = spec.and(MembershipSpecification.filterByCity(membershipFilters.memberMemberAddressCity()));
        spec = spec.and(MembershipSpecification.filterByCountry(membershipFilters.memberMemberAddressCountry()));
        spec = spec.and(MembershipSpecification.filterByMembershipStatus(membershipFilters.membershipStatusNames()));
        spec = spec.and(MembershipSpecification.filterByMembershipTypes(membershipFilters.membershipTypeNames()));
        spec = spec.and(MembershipSpecification.filterByRoleNames(membershipFilters.roleNames(), organizationId));

        if (membershipFilters.startDates() != null && !membershipFilters.startDates().isEmpty()) {
            Date startDateFrom = membershipFilters.startDates().get(0);
            Date startDateTo = membershipFilters.startDates().get(1);
            spec = spec.and(MembershipSpecification.filterByStartDateRange(startDateFrom, startDateTo));
        }

        if (membershipFilters.endDates() != null && !membershipFilters.endDates().isEmpty()) {
            Date endDateFrom = membershipFilters.endDates().get(0);
            Date endDateTo = membershipFilters.endDates().get(1);
            spec = spec.and(MembershipSpecification.filterByEndDateRange(endDateFrom, endDateTo));
        }

        //spec = spec.and(MembershipSpecification.applySorting(combinedSort, organizationId)); // 'combinedSort' from your original code


        Page<Membership> membershipPage = membershipRepository.findAll(spec, pageable);


        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membership -> {
                    System.out.println("Organization Id: " + organizationId + " Member Id: " + membership.getMember().getMemberId());
                    Role memberRole = memberRoleRepository.findRoleByMemberAndOrganization(membership.getMember().getMemberId(), organizationId);
                    return  membershipMapper.fromMembershipWithRole(membership, memberRole);
                })
                .collect(Collectors.toList());


        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
    }


    private boolean isMembershipFiltersEmpty(MembershipFilters filters) {
        return (filters == null) ||
                (filters.memberFirstName() == null || filters.memberFirstName().trim().isEmpty()) &&
                        (filters.memberEmail() == null || filters.memberEmail().trim().isEmpty()) &&
                        (filters.memberMemberAddressCity() == null || filters.memberMemberAddressCity().trim().isEmpty()) &&
                        (filters.memberMemberAddressCountry() == null || filters.memberMemberAddressCountry().trim().isEmpty()) &&
                        (filters.membershipStatusNames() == null || filters.membershipStatusNames().isEmpty()) &&
                        (filters.membershipTypeNames() == null || filters.membershipTypeNames().isEmpty()) &&
                        (filters.roleNames() == null || filters.roleNames().isEmpty()) &&
                        (filters.startDates() == null || filters.startDates().isEmpty()) &&
                        (filters.endDates() == null || filters.endDates().isEmpty());
    }



    public Page<MembershipResponse> getPendingMembershipsByOrganization(
            UUID organizationId,
            Integer pageNo,
            Integer pageSize,
            String sortField,
            String sortOrder) {

        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort firstSort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));

        // Fix for problem: There are items that shows on different pages. Because of same ex. name.
        Sort secondSort = Sort.by(Sort.Order.by(sortField.equals("member.firstName") ? "member.lastName" : "membershipId").with(Sort.Direction.fromString(sortOrder)));

        Sort combinedSort = firstSort.and(secondSort);


        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        Page<Membership> membershipPage = membershipRepository.findPendingMembershipsByOrganizationId(organizationId, pageable);

        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membershipMapper::fromMembership)
                .collect(Collectors.toList());

        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
    }

    public Session getLoginSessionWithGoogle(String googleToken) {
        return null;
    }



}