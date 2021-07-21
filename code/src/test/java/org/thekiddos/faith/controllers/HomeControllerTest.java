package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.services.FreelancerService;
import org.thekiddos.faith.services.ProjectService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class HomeControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;
    @MockBean
    private FreelancerService freelancerService;

    @Autowired
    HomeControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void homePageLoadsOk() throws Exception {
        mockMvc.perform( get( "/" ) )
                .andExpect( status().isOk() );
        Mockito.verify( projectService, Mockito.times( 1 ) ).findFeaturedProjectsDto();
        Mockito.verify( freelancerService, Mockito.times( 1 ) ).findFeaturedFreelancersDto();
    }
}
