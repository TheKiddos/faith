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
    private static int nicknameCounter = 0;
    public static final String SITE_ROOT = "http://localhost:8080/"; // Port must match one from application.properties
    public static final String REGISTRATION_SUCCESSFUL_URL = SITE_ROOT + "register/success";
    public static final String LOGIN_PAGE = SITE_ROOT + "login";
    public static final String ADMIN_PANEL = SITE_ROOT + "admin";
    public static final String USER_ADMIN_PANEL = ADMIN_PANEL + "/users/";
    public static final String FORGOT_PASSWORD_URL = SITE_ROOT + "forgot-password";
    public static final String PASSWORD_RESET_URL = SITE_ROOT + "reset-password";
    public static final String STAKEHOLDER_ROOT = SITE_ROOT + "stakeholder";
    public static final String MY_PROJECTS_PAGE = STAKEHOLDER_ROOT + "/my-projects";
    public static final String ADD_PROJECT_PAGE = MY_PROJECTS_PAGE + "/add";
    public static final String FREELANCER_ROOT = SITE_ROOT + "freelancer";
    public static final String FREELANCER_PROFILE =  FREELANCER_ROOT + "/profile";
    public static final String PUBLIC_PROJECTS_PAGE = SITE_ROOT + "projects";
    public static final String PUBLIC_PROJECT_DETAILS_PAGE_PREFIX = PUBLIC_PROJECTS_PAGE + "/";
    public static final String PROJECT_DASHBOARD_PAGE_PREFIX = "http://localhost:8080/stakeholder/my-projects/";
    public static final String MY_PROPOSALS_PAGE = FREELANCER_ROOT + "/proposals";

    public static String toJson( Object object ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString( object );
    }

    public static User getOrCreateTestUser( UserService userService ) {
        return getOrCreateTestUser( userService, "testuser@test.com", null );
    }

    public static String getPasswordResetTokenUrl( String token ) {
        return PASSWORD_RESET_URL + "/" + token;
    }

    public static User getOrCreateTestUser( UserService userService, String email, String type ) {
        User user;
        try {
            user = (User) userService.loadUserByUsername( email );
            if ( type == null && user.getType() == null )
                return user;
            if ( !user.getType().toString().equals( type ) ) {
                userService.rejectUser( user.getNickname() );
                throw new UsernameNotFoundException( "Die" );
            }
        }
        catch ( UsernameNotFoundException e ) {
            var password = "password";
            UserDto userDto = UserDto.builder().email( email )
                    .password( password )
                    .passwordConfirm( password )
                    .nickname( "tasty" + ++nicknameCounter )
                    .firstName( "Test" )
                    .lastName( "User" )
                    .civilId( new byte[]{} )
                    .phoneNumber( "+963987654321" )
                    .address( "Street" )
                    .type( type )
                    .build();
            user = userService.createUser( userDto );
        }
        return user;
    }

    public static String getProjectDetailsPage( Long projectId ) {
        return PUBLIC_PROJECT_DETAILS_PAGE_PREFIX + projectId;
    }

    public static String getProjectDashboardPage( Long id ) {
        return PROJECT_DASHBOARD_PAGE_PREFIX + id;
    }
}
