package org.thekiddos.faith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities Class for functional tests
 */
public final class Utils {
    public static final String SITE_ROOT = "http://localhost:8080/"; // Port must match one from application.properties
    public static final String REGISTRATION_SUCCESSFUL_URL = "http://localhost:8080/register/success";

    public static String toJson( Object object ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString( object );
    }
}
