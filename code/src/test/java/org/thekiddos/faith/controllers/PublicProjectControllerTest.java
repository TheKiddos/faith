package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import java.time.Duration;
import java.util.List;

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
    @MockBean
    private UserService userService;

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
        Project project = getTestProject();

        Mockito.doReturn( project ).when( projectService ).findById( project.getId() );
        Mockito.doReturn( List.of() ).when( bidService ).findByProjectDto( project );
        mockMvc.perform( get( "/projects/" + project.getId() ) )
                .andExpect( status().isOk() );

        Mockito.verify( userService, Mockito.times( 0 ) ).loadUserByUsername( any() );
        Mockito.verify( bidService, Mockito.times( 0 ) ).canBidOnProject( any(), any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    public void projectDetailsFreelancer() throws Exception {
        Project project = getTestProject();
        User user = getTestUser();

        Mockito.doReturn( project ).when( projectService ).findById( project.getId() );
        Mockito.doReturn( List.of() ).when( bidService ).findByProjectDto( project );
        Mockito.doReturn( user ).when( userService ).loadUserByUsername( user.getEmail() );
        Mockito.doReturn( true ).when( bidService ).canBidOnProject( user, project );
        mockMvc.perform( get( "/projects/" + project.getId() ) )
                .andExpect( status().isOk() );

        Mockito.verify( userService, Mockito.times( 1 ) ).loadUserByUsername( user.getEmail() );
        Mockito.verify( bidService, Mockito.times( 1 ) ).canBidOnProject( user, project );
    }

    private User getTestUser() {
        User user = new User();
        user.setEmail( "freelancer@test.com" );
        user.setType( new Freelancer() );
        return user;
    }

    private Project getTestProject() {
        Project project = new Project();
        project.setId( 1L );
        project.setName( "Hello" );
        project.setAllowBidding( true );
        project.setOwner( new Stakeholder() );
        project.setDescription( "hhhhhhhhh" );
        project.setDuration( Duration.ofDays( 2L ) );
        project.setPreferredBid( 200 );
        project.setMinimumQualification( 200 );
        return project;
    }
}
