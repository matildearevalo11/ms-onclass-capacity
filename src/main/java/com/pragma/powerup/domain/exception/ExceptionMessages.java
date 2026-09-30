package com.pragma.powerup.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    INVALID_REQUEST("The request body is invalid."),
    TECHNOLOGIES_NOT_FOUND("One or more technologies do not exist."),
    TECHNOLOGIES_ALREADY_ASSOCIATED("The capacity already has associated technologies."),
    TECHNOLOGY_SERVICE_UNAVAILABLE("The technology service is unavailable."),
    AUTHENTICATION_REQUIRED("A valid token is required to access this resource."),
    ACCESS_DENIED("The user does not have permission to perform this action."),
    DATABASE_UNAVAILABLE("The persistence operation could not be completed."),
    INTERNAL_ERROR("The request could not be completed.");
    private final String message;
}
