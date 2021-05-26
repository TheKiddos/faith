package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class StakeholderControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Autowired
    StakeholderControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void myProjectsPageLoadsOk() throws Exception {
        User user = getTestUser();
        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );
        Mockito.doReturn( List.of() ).when( projectService ).findByOwnerDto( (Stakeholder) user.getType() );

        mockMvc.perform( get(  "/stakeholder/my-projects" ) )
                .andExpect( status().isOk() );

        Mockito.verify( projectService, Mockito.times( 1 ) ).findByOwnerDto( (Stakeholder) user.getType() );
    }

    @Test
    @WithMockUser( authorities = {"USER"} )
    void myProjectsPageRequiresStakeholder() throws Exception {
        mockMvc.perform( get(  "/stakeholder/my-projects" ) )
                .andExpect( status().isForbidden() );
        Mockito.verify( projectService, Mockito.times( 0 ) ).findByOwnerDto( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"} )
    void createNewProjectPageLoadsOk() throws Exception {
        mockMvc.perform( get(  "/stakeholder/my-projects/add" ) )
                .andExpect( status().isOk() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void createProject() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        User user = getTestUser();
        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );

        mockMvc.perform(post( "/stakeholder/my-projects/add" )
                .with( csrf() )
                .param( "name", projectDto.getName() )
                .param( "description", projectDto.getDescription() )
                .param( "preferredBid", String.valueOf( projectDto.getPreferredBid() ) )
                .param( "duration", String.valueOf( projectDto.getDuration() ) )
                .param( "minimumQualification", String.valueOf( projectDto.getMinimumQualification() ) )
                .param( "allowBidding", String.valueOf( projectDto.isAllowBidding() ) ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

        Mockito.verify( projectService, Mockito.times( 1 ) ).createProjectFor( (Stakeholder)user.getType(), projectDto );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void createProjectInvalid() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name( "" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        mockMvc.perform(post( "/stakeholder/my-projects/add" )
                .with( csrf() )
                .param( "name", projectDto.getName() )
                .param( "description", projectDto.getDescription() )
                .param( "preferredBid", String.valueOf( projectDto.getPreferredBid() ) )
                .param( "duration", String.valueOf( projectDto.getDuration() ) )
                .param( "minimumQualification", String.valueOf( projectDto.getMinimumQualification() ) )
                .param( "allowBidding", String.valueOf( projectDto.isAllowBidding() ) ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( projectService, Mockito.times( 0 ) ).createProjectFor( any(), any() );
    }

    private User getTestUser() {
        UserDto userDto = UserDto.builder().email( "stakeholder@test.com" )
                .password( "password" )
                .passwordConfirm( "password" )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Stakeholder" )
                .build();

        return userMapper.userDtoToUser( userDto );
    }
}
