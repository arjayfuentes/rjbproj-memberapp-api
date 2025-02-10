package com.rjproj.memberapp.service;

import com.rjproj.memberapp.dto.*;
import com.rjproj.memberapp.exception.MemberException;
import com.rjproj.memberapp.exception.MemberExceptionHandler;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.rjproj.memberapp.exception.MemberErrorMessage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private RoleRepository roleRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;  // Injecting google client-id from application.yml

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    private final RestTemplate restTemplate;

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

    public MemberResponse registerMemberWithGoogle(String googleToken) {
        GoogleTokenInfo tokenInfo = validateGoogleToken(googleToken);

        Optional<Member> retrievedMember = memberRepository.findByEmail(tokenInfo.email());
        if (retrievedMember.isPresent()){
            throw new MemberException("Member with email address " + tokenInfo.email() + " already exists", MEMBER_EXISTS.getMessage(), HttpStatus.CONFLICT);
        }

        // Step 3: Register the new user if they don't exist
        Member newMember = new Member();
        newMember.setEmail(tokenInfo.email());
        newMember.setFirstName(tokenInfo.name());
        newMember = memberRepository.save(newMember);

        return memberMapper.fromMember(memberRepository.save(newMember));
    }


    private GoogleTokenInfo validateGoogleToken(String googleToken) {
        // Step to validate the Google token with Google's API and get user details
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + googleToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleTokenInfo> response = restTemplate.getForEntity(googleApiUrl, GoogleTokenInfo.class);

        return response.getBody();
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
                throw new MemberException("The email address you provided does not exists.", MEMBER_NOT_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
            }

            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequest.email());

            MemberResponse memberResponse = memberMapper.fromMember(member.get());
            Role activeRole = null;
            RoleResponse roleResponse = null;
            List<String> preLogInPermissions = new ArrayList<>();
            OrganizationResponse activeOrganization = null;
            List<UUID> organizationIdsOfMember = membershipService.getActiveOrganizationIdsByMemberId(member.get().getMemberId());
            MembershipResponse activeMembership = null;

            UUID activeOrganizationId = null;

            String jwt = null;

            if(organizationIdsOfMember.size() == 1) {
                activeOrganizationId = organizationIdsOfMember.getFirst();

                activeRole = memberRoleRepository.findRolesByMemberAndOrganization(member.get().getMemberId(), activeOrganizationId);
                roleResponse = roleMapper.fromRole(activeRole);
                preLogInPermissions = activeRole.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList());
                activeMembership =  membershipMapper.fromMembership(membershipService.getMembership(member.get().getMemberId(), activeOrganizationId));
                jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions, activeOrganizationId, member.get().getMemberId());

                activeOrganization = this.organizationClient.findMyOrganizationById(activeOrganizationId);

            } else {
                preLogInPermissions.add("com.rjproj.memberapp.permission.organization.viewAll");
            }


            jwt = jwtUtil.generateToken(userDetails.getUsername(), activeRole, preLogInPermissions, activeOrganizationId, member.get().getMemberId());

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
            System.out.println(e);
            throw new MemberException("Incorrect password", PASSWORD_INCORRECT.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
                activeRole = memberRoleRepository.findRolesByMemberAndOrganization(memberId, selectedOrganizationId);
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
            Role activeRole = memberRoleRepository.findRolesByMemberAndOrganization(selectOrganizationRequest.memberId(), selectOrganizationRequest.organizationId());
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
            String sortOrder) {

        OrganizationResponse organization = organizationClient.findOrganizationById(organizationId);
        if (organization == null) {
            throw new RuntimeException("Organization not found");
        }

        Sort sort = Sort.unsorted();

        // Check if the sortField is 'role.name' and set the appropriate sorting
        if ("role.name".equals(sortField)) {
            sort = Sort.by(Sort.Order.by("r.name").with(Sort.Direction.fromString(sortOrder)));
        } else {
            sort = Sort.by(Sort.Order.by(sortField).with(Sort.Direction.fromString(sortOrder)));
        }

        // Fix for problem: Sorting secondary criteria (to prevent duplicates on pages)
        Sort secondarySort = Sort.by(Sort.Order.by(sortField.equals("member.firstName") ? "member.lastName" : "membershipId").with(Sort.Direction.fromString(sortOrder)));
        Sort combinedSort = sort.and(secondarySort);

        Pageable pageable = PageRequest.of(pageNo, pageSize, combinedSort);

        // Handle paginated query based on sorting by 'role.name' or default
        Page<Membership> membershipPage;
        if ("role.name".equals(sortField)) {
            membershipPage = membershipRepository.findMembershipsByOrganizationIdSortedByRoleName(organizationId, pageable);
        } else {
            membershipPage = membershipRepository.findMembershipsByOrganizationId(organizationId, pageable);
        }


        List<MembershipResponse> membershipResponses = membershipPage.getContent().stream()
                .map(membership -> {
                    Role memberRole = memberRoleRepository.findRolesByMemberAndOrganization(membership.getMember().getMemberId(), organizationId);
                    return  membershipMapper.fromMembershipWithRole(membership, memberRole);
                })
                .collect(Collectors.toList());


        return new PageImpl<>(membershipResponses, pageable, membershipPage.getTotalElements());
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

    public Session loginMemberWithGoogle(@Valid String googleToken) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + googleToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleTokenInfo> response = restTemplate.getForEntity(googleApiUrl, GoogleTokenInfo.class);

        GoogleTokenInfo tokenInfo = response.getBody();

        if (tokenInfo != null && tokenInfo.aud().equals(googleClientId)) {
//            List<GoogleTokenInfo.Address> addresses = getAddressFromGoogleAPI(googleToken);
//            tokenInfo = new GoogleTokenInfo(
//                    tokenInfo.sub(),
//                    tokenInfo.aud(),
//                    tokenInfo.email(),
//                    tokenInfo.name(),
//                    tokenInfo.given_name(),
//                    tokenInfo.family_name(),
//                    tokenInfo.picture(),
//                    tokenInfo.locale(),
//                    tokenInfo.hd(),
//                    tokenInfo.iat(),
//                    tokenInfo.exp(),
//                    addresses // Include addresses
//            );
            Optional<Member> verifiedMember = memberRepository.findByEmail(tokenInfo.email());

            if(!verifiedMember.isPresent()) {
                throw new MemberException(
                        "The google account you provided does not exists. Sign up first to contiue",
                        MEMBER_NOT_EXISTS.getMessage(),
                        HttpStatus.BAD_REQUEST);
            }
            Member member = verifiedMember.get();
            member.setEmail(tokenInfo.email());
            member.setFirstName(tokenInfo.name());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getEmail(), null, new ArrayList<>());

            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(tokenInfo.email());

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

                activeRole = memberRoleRepository.findRolesByMemberAndOrganization(member.getMemberId(), activeOrganizationId);
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
        } else {
            throw new RuntimeException("Invalid Google token.");
        }
    }


    private List<GoogleTokenInfo.Address> getAddressFromGoogleAPI(String googleToken) {
        String accessToken = getAccessTokenFromIdToken(googleToken);

        String apiUrl = "https://people.googleapis.com/v1/people/me?personFields=addresses";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);  // Use the access token

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> addressesData = (List<Map<String, Object>>) body.get("addresses");

            List<GoogleTokenInfo.Address> addresses = new ArrayList<>();

            for (Map<String, Object> addressData : addressesData) {
                String streetAddress = (String) addressData.get("streetAddress");
                String city = (String) addressData.get("city");
                String region = (String) addressData.get("region");
                String postalCode = (String) addressData.get("postalCode");
                String country = (String) addressData.get("country");

                GoogleTokenInfo.Address address = new GoogleTokenInfo.Address(streetAddress, city, region, postalCode, country);
                addresses.add(address);
            }

            return addresses;

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public String getAccessTokenFromIdToken(String googleToken) {
        String googleTokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id_token", googleToken);
        map.add("client_id", "654581949282-dmvkqbivaa8rmvem7ipjbas30p5akkrm.apps.googleusercontent.com");  // Your app's client ID
        map.add("client_secret", "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");  // Your app's client secret
        map.add("grant_type", "authorization_code");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUrl, map, Map.class);

        // Extract the access token from the response
        Map<String, String> responseBody = response.getBody();
        return responseBody.get("access_token");
    }

}