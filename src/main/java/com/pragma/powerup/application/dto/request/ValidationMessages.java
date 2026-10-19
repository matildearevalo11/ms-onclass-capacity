package com.pragma.powerup.application.dto.request;

public final class ValidationMessages {
    public static final String PAGE_MIN = "Page must be zero or greater.";
    public static final String SIZE_RANGE = "Size must be between 1 and 100.";
    public static final String CAPACITY_IDS_REQUIRED = "Capacity IDs are required.";
    public static final String CAPACITY_ID_REQUIRED = "Capacity ID is required.";
    public static final String CAPACITY_ID_POSITIVE = "Capacity ID must be positive.";

    private ValidationMessages() {
    }
}
