package com.rjproj.memberapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MemberException extends RuntimeException {

    private final String message;
}