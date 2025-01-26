package com.rjproj.memberapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizationNotFoundException extends RuntimeException {

    private final String msg;
}
