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
class StakeholderControllerTest {
    private final MockMvc mockMvc;

    @Autowired
    StakeholderControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"} )
    void myProjectsPageLoadsOk() throws Exception {
        mockMvc.perform( get(  "/stakeholder/my-projects" ) )
                .andExpect( status().isOk() );
    }

    @Test
    @WithMockUser( authorities = {"USER"} )
    void myProjectsPageRequiresStakeholder() throws Exception {
        mockMvc.perform( get(  "/stakeholder/my-projects" ) )
                .andExpect( status().isForbidden() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"} )
    void createNewProjectPageLoadsOk() throws Exception {
        mockMvc.perform( get(  "/stakeholder/my-projects/add" ) )
                .andExpect( status().isOk() );
    }
}
