package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.services.ProjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class PublicProjectControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    PublicProjectControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void projectList() throws Exception {
        mockMvc.perform( get( "/projects" ) )
                .andExpect( status().isOk() );
        Mockito.verify( projectService, Mockito.times( 1 ) ).findAllDto();
    }
}
