package com.rjproj.memberapp.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public record MembershipFilters(
        String memberFirstName,
        String memberEmail,
        String memberMemberAddressCity,
        String memberMemberAddressCountry,
        List<String> membershipStatusNames,
        List<String> membershipTypeNames,
        String roleName,
        List<Date> startDates,
        List<Date> endDates
) {
}
