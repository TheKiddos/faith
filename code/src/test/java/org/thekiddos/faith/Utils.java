package org.thekiddos.faith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities Class for functional tests
 */
public final class Utils {
    public static final String SITE_ROOT = "http://localhost:8080/"; // Port must match one from application.properties
    public static final String REGISTRATION_SUCCESSFUL_URL = SITE_ROOT + "register/success";
    public static final String LOGIN_PAGE = SITE_ROOT + "login";
    public static final String ADMIN_PANEL = SITE_ROOT + "admin";
    public static final String USER_ADMIN_PANEL = ADMIN_PANEL + "/users/";

    public static String toJson( Object object ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString( object );
    }
}
