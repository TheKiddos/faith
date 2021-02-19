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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @WithMockUser( roles = {"ADMIN"} )
    void adminPanelLoadsOk() throws Exception {
        mockMvc.perform( get( "/admin" ) ).andExpect( status().isOk() );
    }

    @Test
    @WithMockUser
    void adminPanelRequiresAdminRole() throws Exception {
        mockMvc.perform( get( "/admin" ) ).andExpect( status().isForbidden() );
    }

    @Test
    @WithMockUser( roles = {"ADMIN"} )
    void usersAdminPanelLoadsOk() throws Exception {
        mockMvc.perform( get( "/admin/users" ) ).andExpect( status().isOk() );

        Mockito.verify( userService, Mockito.times( 1 ) ).getAll();
    }
}