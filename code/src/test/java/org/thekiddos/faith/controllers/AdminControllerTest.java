package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.services.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AdminControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    AdminControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser( authorities = {"ADMIN"} )
    void adminPanelLoadsOk() throws Exception {
        mockMvc.perform( get( "/admin" ) ).andExpect( status().isOk() );
    }

    @Test
    @WithMockUser
    void adminPanelRequiresAdminRole() throws Exception {
        mockMvc.perform( get( "/admin" ) ).andExpect( status().isForbidden() );
    }

    @Test
    @WithMockUser( authorities = {"ADMIN"} )
    void usersAdminPanelLoadsOk() throws Exception {
        mockMvc.perform( get( "/admin/users" ) ).andExpect( status().isOk() );

        Mockito.verify( userService, Mockito.times( 1 ) ).getAll();
    }

    @Test
    @WithMockUser( authorities = {"ADMIN"} )
    void activateUserAccount() throws Exception {
        mockMvc.perform( post("/admin/users")
                .with( csrf() )
                .param( "nickname", "someidiot" ) )
                .andExpect( status().is3xxRedirection() );

        Mockito.verify( userService, Mockito.times( 1 ) ).activateUser( "someidiot" );
    }
}
