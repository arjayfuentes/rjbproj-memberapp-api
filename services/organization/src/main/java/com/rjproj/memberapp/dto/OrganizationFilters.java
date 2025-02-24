package com.rjproj.memberapp.dto;

import java.util.Date;
import java.util.List;

public record OrganizationFilters(
        String firstName,
        String email,
        String memberMemberAddressCity,
        String membershipStatusName,
        String membershipTypeName,
        String roleName,
        List<Date> startDates,
        List<Date> endDates
) {
}
