package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.models.PasswordResetToken;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.PasswordResetTokenRepository;
import org.thekiddos.faith.services.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class LoginControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    LoginControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    void loginPageLoadsSuccessfully() throws Exception {
        mockMvc.perform( get( "/login" ) ).andExpect( status().isOk() );
    }

    @Test
    public void loginWithValidUserThenAuthenticated() throws Exception {
        User admin = new User();
        admin.setAdmin( true );
        admin.setEmail( "admin@faith.com" );
        admin.setPassword( "Admin@Fa1ith" );
        Mockito.doReturn( admin ).when( userService ).loadUserByUsername( "admin@faith.com" );

        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = formLogin()
                .userParameter( "email" )
                .user( "admin@faith.com" )
                .password( "Admin@Fa1ith" );

        mockMvc.perform( login )
                .andExpect( authenticated().withUsername( "admin@faith.com" ) )
                .andExpect( status().is3xxRedirection() );
    }

    @Test
    void adminRedirectsToAdminPanel() throws Exception {
        User admin = new User();
        admin.setAdmin( true );
        admin.setEmail( "admin@faith.com" );
        admin.setPassword( "Admin@Fa1ith" );
        Mockito.doReturn( admin ).when( userService ).loadUserByUsername( "admin@faith.com" );

        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = formLogin()
                .userParameter( "email" )
                .user( "admin@faith.com" )
                .password( "Admin@Fa1ith" );

        mockMvc.perform( login )
                .andExpect( authenticated().withUsername( "admin@faith.com" ) )
                .andExpect( status().is3xxRedirection() )
                .andExpect( redirectedUrl("/admin") );
    }

    @Test
    void normalUserRedirectsToHomePage() throws Exception {
        User user = new User();
        user.setEmail( "user@faith.com" );
        user.setPassword( "Admin@Fa1ith" );
        Mockito.doReturn( user ).when( userService ).loadUserByUsername( "user@faith.com" );

        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = formLogin()
                .userParameter( "email" )
                .user( "user@faith.com" )
                .password( "Admin@Fa1ith" );

        mockMvc.perform( login )
                .andExpect( authenticated().withUsername( "user@faith.com" ) )
                .andExpect( status().is3xxRedirection() )
                .andExpect( redirectedUrl("/") );
    }

    @Test
    public void loginWithInvalidUserThenUnauthenticated() throws Exception {
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = formLogin()
                .userParameter( "email" )
                .user( "invalid" )
                .password( "invalidpassword" );

        mockMvc.perform( login )
                .andExpect( unauthenticated() )
                .andExpect( status().is3xxRedirection() );
    }

    @Test
    void forgotPasswordPageLoadsOk() throws Exception {
        mockMvc.perform( get( "/forgot-password" ) ).andExpect( status().isOk() );
    }

    @Test
    void generateToken() throws Exception {
        mockMvc.perform( post( "/forgot-password" ).with( csrf() ).param( "email", "test@test.com" ) ).andExpect( status().isOk() );
        Mockito.verify( userService, Mockito.times( 1 ) ).createForgotPasswordToken( "test@test.com" );
    }

    @Test
    void resetPasswordPageRequiresToken() throws Exception {
        mockMvc.perform( get( "/reset-password/23123-sadas" ) ).andExpect( status().isNotFound() );
    }

    @Test
    void resetPasswordPageLoadsOk() throws Exception {
        String token = "secret-1234";
        Mockito.doReturn( Optional.of( new PasswordResetToken() ) ).when( passwordResetTokenRepository ).findByToken( token );
        mockMvc.perform( get( "/reset-password/" + token ) ).andExpect( status().isOk() );
    }

    @Test
    void changeUserPassword() throws Exception {
        String token = "secret-1234";
        Mockito.doReturn( Optional.of( new PasswordResetToken() ) ).when( passwordResetTokenRepository ).findByToken( token );
        mockMvc.perform( post( "/reset-password/" + token )
                        .with( csrf() )
                        .param( "password", "newpassword" )
                        .param( "passwordConfirm", "newpassword" )
        ).andExpect( status().is3xxRedirection() );
        Mockito.verify( userService, Mockito.times( 1 ) ).resetUserPassword( token, "newpassword" );
    }

    @Test
    void changeUserPasswordRequiresToken() throws Exception {
        String token = "secret-1234";
        mockMvc.perform( post( "/reset-password/" + token )
                .with( csrf() )
                .param( "password", "newpassword" )
                .param( "password-confirm", "newpassword" )
        ).andExpect( status().isNotFound() );
        Mockito.verify( userService, Mockito.times( 0 ) ).resetUserPassword( any(), any() );
    }

    @Test
    void changeUserPasswordNeedsPasswordMatches() throws Exception {
        String token = "secret-1234";
        Mockito.doReturn( Optional.of( new PasswordResetToken() ) ).when( passwordResetTokenRepository ).findByToken( token );
        mockMvc.perform( post( "/reset-password/" + token )
                .with( csrf() )
                .param( "password", "newpassword" )
                .param( "password-confirm", "newpassword1" )
        ).andExpect( status().isOk() );
        Mockito.verify( userService, Mockito.times( 0 ) ).resetUserPassword( any(), any() );
    }
}
