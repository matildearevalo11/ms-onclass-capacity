package com.pragma.powerup.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    INVALID_REQUEST("The request body is invalid."),
    CAPACITY_NAME_REQUIRED("Capacity name is required."),
    CAPACITY_DESCRIPTION_REQUIRED("Capacity description is required."),
    TECHNOLOGY_COUNT_OUT_OF_RANGE("A capacity must have between 3 and 20 technologies."),
    TECHNOLOGY_ID_INVALID("Technology ID must be positive."),
    TECHNOLOGIES_REPEATED("Technologies must not be repeated."),
    INVALID_SORT_FIELD("Sort by name or technologyCount."),
    INVALID_SORT_DIRECTION("Direction must be asc or desc."),
    TECHNOLOGIES_NOT_FOUND("One or more technologies do not exist."),
    TECHNOLOGIES_ALREADY_ASSOCIATED("The capacity already has associated technologies."),
    TECHNOLOGY_SERVICE_UNAVAILABLE("The technology service is unavailable."),
    AUTHENTICATION_REQUIRED("A valid token is required to access this resource."),
    ACCESS_DENIED("The user does not have permission to perform this action."),
    DATABASE_UNAVAILABLE("The persistence operation could not be completed."),
    INTERNAL_ERROR("The request could not be completed.");
    private final String message;
}
