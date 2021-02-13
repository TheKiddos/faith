package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AdminControllerTest {
    private final MockMvc mockMvc;

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
    void adminPanelRedirectToLoginIfNotAdmin() throws Exception {
        mockMvc.perform( get( "/admin" ) ).andExpect( status().isForbidden() );
    }
}
