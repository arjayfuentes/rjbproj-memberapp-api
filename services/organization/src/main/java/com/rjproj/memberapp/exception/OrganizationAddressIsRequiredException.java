package com.rjproj.memberapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizationAddressIsRequiredException extends RuntimeException {

    private final String msg;
}

