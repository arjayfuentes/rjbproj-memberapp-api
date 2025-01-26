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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    public MemberResponse createMember(@Valid MemberRequest memberRequest) {
        Member member = memberMapper.toMember(memberRequest);
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
}
