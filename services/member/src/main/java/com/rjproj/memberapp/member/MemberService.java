package com.rjproj.memberapp.member;

import com.rjproj.memberapp.exception.MemberNotFoundException;
import jakarta.validation.Valid;
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

    public UUID addMember(@Valid MemberRequest memberRequest) {
        Member member = memberRepository.save(memberMapper.toMember(memberRequest));
        return member.getMemberId();
    }

    public void updateMember(@Valid MemberRequest memberRequest) {
        Member member = memberRepository.findById(memberRequest.memberId())
                .orElseThrow(() -> new MemberNotFoundException(
                    String.format("Cannot update member with id %s", memberRequest.memberId())
                ));
        mergeMember(member, memberRequest);
        memberRepository.save(member);
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

    public List<MemberResponse> findAllMember() {
        return memberRepository.findAll()
                .stream()
                .map(memberMapper::fromMember)
                .collect(Collectors.toList());
    }

    public MemberResponse findById(UUID memberId) {
        return memberRepository.findById(memberId)
                .map(memberMapper::fromMember)
                .orElseThrow(() -> new MemberNotFoundException(
                        String.format("No member found with the provided ID: %s", memberId))
                );
    }

    public Boolean existsById(UUID memberId) {
        return memberRepository.findById(memberId)
                .isPresent();
    }

    public void deleteMember(UUID memberId) {
        memberRepository.deleteById(memberId);
    }


}
