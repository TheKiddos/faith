package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class LoginControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
}
