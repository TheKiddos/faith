package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;
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
    @MockBean
    private BidService bidService;
    @MockBean
    private BidCommentService bidCommentService;

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

    @Test
    public void projectDetails() throws Exception {
        mockMvc.perform( get( "/projects/1" ) )
                .andExpect( status().isOk() );
        Mockito.verify( projectService, Mockito.times( 1 ) ).findById( 1L );
        Mockito.verify( bidService, Mockito.times( 1 ) ).findByProjectDto( any() );
    }

}
