package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.ProposalService;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ProposalControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private ProposalService proposalService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    private User stakeholderUser;
    private User freelanecrUser;

    @Autowired
    ProposalControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
        this.stakeholderUser = getTestUser( "Stakeholder" );
        this.freelanecrUser = getTestUser( "Freelancer" );
    }

    @Test
    @WithMockUser( authorities = {"USER"})
    void sendProposalNotStakeholder() throws Exception {
        double amount = 10.0;

        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( 1 )
                           .projectId( 1 )
                           .build();

        mockMvc.perform( post( "/stakeholder/propose" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "freelancerId", String.valueOf( dto.getFreelancerId() ) )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isForbidden() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 0 ) ).sendProposal( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void sendProposal() throws Exception {
        double amount = 10.0;

        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( 1 )
                           .projectId( 1 )
                           .build();

        Mockito.doReturn( Optional.of( stakeholderUser ) ).when( userRepository ).findById( stakeholderUser.getEmail() );

        mockMvc.perform( post( "/stakeholder/propose" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "freelancerId", String.valueOf( dto.getFreelancerId() ) )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isCreated() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 1 ) ).sendProposal( dto );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void sendProposalUsesValidation() throws Exception {
        double amount = -100.0;

        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( 1 )
                           .projectId( 1 )
                           .build();

        Mockito.doReturn( Optional.of( stakeholderUser ) ).when( userRepository ).findById( stakeholderUser.getEmail() );

        mockMvc.perform( post( "/stakeholder/propose" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "freelancerId", String.valueOf( dto.getFreelancerId() ) )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isBadRequest() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 0 ) ).sendProposal( dto );
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void sendProposalReturnsBadRequestOnException() throws Exception {
        double amount = 100.0;

        ProposalDto dto = ProposalDto.builder()
                           .amount( amount )
                           .freelancerId( 1 )
                           .projectId( 1 )
                           .build();

        Mockito.doReturn( Optional.of( stakeholderUser ) ).when( userRepository ).findById( stakeholderUser.getEmail() );
        Mockito.doThrow( ProjectNotFoundException.class ).when( proposalService ).sendProposal( dto );

        mockMvc.perform( post( "/stakeholder/propose" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "freelancerId", String.valueOf( dto.getFreelancerId() ) )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isBadRequest() )
                .andReturn();
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"})
    void getFreelancerProposalsNotFreelancer() throws Exception {
        mockMvc.perform( get( "/freelancer/proposals" ) )
                .andExpect( status().isForbidden() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 0 ) ).findFreelancerProposals( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void getFreelancerProposals() throws Exception {

        Mockito.doReturn( Optional.of( freelanecrUser ) ).when( userRepository ).findById( freelanecrUser.getEmail() );

        mockMvc.perform( get( "/freelancer/proposals" ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 1 ) ).findFreelancerProposals( any() );
    }
    
    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"})
    void getFreelancerProposalsCountNotFreelancer() throws Exception {
        mockMvc.perform( get( "/freelancer/proposals/count" ) )
                .andExpect( status().isForbidden() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 0 ) ).findFreelancerProposals( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void getFreelancerProposalsCount() throws Exception {

        Mockito.doReturn( Optional.of( freelanecrUser ) ).when( userRepository ).findById( freelanecrUser.getEmail() );
        Mockito.doReturn( List.of( null, null ) ).when( proposalService ).findFreelancerProposals( (Freelancer) freelanerUser.getType() );
        

        mockMvc.perform( get( "/freelancer/proposals" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$[*]", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$['proposalsCount']", is( 2 ) ) )
                .andReturn();
    }

    private User getTestUser( String type ) {
        var email = type.equalsIgnoreCase( "Stakeholder" ) ? "stakeholder@test.com" : "freelancer@test.com";
        UserDto userDto = UserDto.builder().email( email )
                .password( "password" )
                .passwordConfirm( "password" )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( type )
                .build();

        return userMapper.userDtoToUser( userDto );
    }
}
