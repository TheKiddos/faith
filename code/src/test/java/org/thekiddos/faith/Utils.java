package org.thekiddos.faith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

/**
 * Utilities Class for functional tests
 */
public final class Utils {
    public static final String SITE_ROOT = "http://localhost:8080/"; // Port must match one from application.properties
    public static final String REGISTRATION_SUCCESSFUL_URL = SITE_ROOT + "register/success";
    public static final String LOGIN_PAGE = SITE_ROOT + "login";
    public static final String ADMIN_PANEL = SITE_ROOT + "admin";
    public static final String USER_ADMIN_PANEL = ADMIN_PANEL + "/users/";
    public static final String FORGOT_PASSWORD_URL = SITE_ROOT + "forgot-password";
    public static final String PASSWORD_RESET_URL = SITE_ROOT + "reset-password";

    public static String toJson( Object object ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString( object );
    }

    public static User getOrCreateTestUser( UserService userService ) {
        try {
            return (User) userService.loadUserByUsername( "testuser@test.com" );
        }
        catch ( UsernameNotFoundException e ) {
            String password = "password";
            UserDto userDto = UserDto.builder().email( "testuser@test.com" )
                    .password( password )
                    .passwordConfirm( password )
                    .nickname( "tasty" )
                    .firstName( "Test" )
                    .lastName( "User" )
                    .civilId( new byte[]{} )
                    .phoneNumber( "+963987654321" )
                    .address( "Street" )
                    .type( null )
                    .build();
            return userService.createUser( userDto );
        }
    }

    public static String getPasswordResetTokenUrl( String token ) {
        return PASSWORD_RESET_URL + "/" + token;
    }
}
