package com.pragma.powerup.infrastructure.constants;

public final class ApiPaths {
    public static final String CAPACITIES = "/capacities";
    public static final String CAPACITY_IDS = CAPACITIES + "/ids";
    public static final String CAPACITY_DETAILS = CAPACITIES + "/details";
    public static final String TECHNOLOGY_CAPACITIES = "/technologies/capacities";
    public static final String OPENAPI = "/openapi.yaml";
    public static final String OPENAPI_FILE = "openapi.yaml";

    private ApiPaths() {
    }

    public static String[] documentation() {
        return new String[] {OPENAPI, "/v3/api-docs", "/v3/api-docs/swagger-config", "/webjars/swagger-ui/**",
                "/swagger-ui/**", "/swagger-ui.html"};
    }
}
