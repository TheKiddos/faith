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
import org.thekiddos.faith.exceptions.InvalidTransitionException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.exceptions.ProposalNotAllowedException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Proposal;
import org.thekiddos.faith.models.Status;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.ProposalService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private User freelancerUser;

    @Autowired
    ProposalControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
        this.stakeholderUser = getTestUser( "Stakeholder" );
        this.freelancerUser = getTestUser( "Freelancer" );
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

        Mockito.doReturn( null ).when( projectService ).findByIdForOwnerDto( any(), anyLong() );
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

        Mockito.verify( proposalService, Mockito.times( 0 ) ).findNewFreelancerProposals( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void getFreelancerProposals() throws Exception {

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );

        mockMvc.perform( get( "/freelancer/proposals" ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 1 ) ).findNewFreelancerProposals( any() );
    }
    
    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"})
    void getFreelancerProposalsCountNotFreelancer() throws Exception {
        mockMvc.perform( get( "/freelancer/proposals/count" ) )
                .andExpect( status().isForbidden() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 0 ) ).findNewFreelancerProposals( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void getFreelancerProposalsCount() throws Exception {

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doReturn( List.of( new ProposalDto(), new ProposalDto() ) ).when( proposalService ).findNewFreelancerProposals( (Freelancer) freelancerUser.getType() );
        

        mockMvc.perform( get( "/freelancer/proposals/count" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$[*]", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$['proposalsCount']", is( 2 ) ) )
                .andReturn();
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void rejectProposalRequiresFreelancer() throws Exception {

        Mockito.doReturn( Optional.of( stakeholderUser ) ).when( userRepository ).findById( stakeholderUser.getEmail() );

        mockMvc.perform( post( "/freelancer/proposals/reject/1" )
                .with( csrf() ) )
                .andExpect( status().isForbidden() )
                .andReturn();
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void rejectProposal() throws Exception {

        var proposal = new Proposal();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doReturn( proposal ).when( proposalService ).findProposalFor( (Freelancer) freelancerUser.getType(), 1L );

        mockMvc.perform( post( "/freelancer/proposals/reject/1" )
                .with( csrf() ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 1 ) ).setStatus( proposal, Status.REJECTED );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void rejectProposalInvalidTransition() throws Exception {

        var proposal = new Proposal();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doReturn( proposal ).when( proposalService ).findProposalFor( (Freelancer) freelancerUser.getType(), 1L );
        Mockito.doThrow( InvalidTransitionException.class ).when( proposalService ).setStatus( proposal, Status.REJECTED );

        mockMvc.perform( post( "/freelancer/proposals/reject/1" )
                .with( csrf() ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void rejectProposalNotFound() throws Exception {

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doThrow( ProposalNotAllowedException.class ).when( proposalService ).findProposalFor( (Freelancer) freelancerUser.getType(), 1L );

        mockMvc.perform( post( "/freelancer/proposals/reject/1" )
                .with( csrf() ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();
    }

    @Test
    @WithMockUser( authorities = {"USER", "STAKEHOLDER"}, username = "stakeholder@test.com")
    void acceptProposalRequiresFreelancer() throws Exception {

        Mockito.doReturn( Optional.of( stakeholderUser ) ).when( userRepository ).findById( stakeholderUser.getEmail() );

        mockMvc.perform( post( "/freelancer/proposals/accept/1" )
                         .with( csrf() ) )
                .andExpect( status().isForbidden() )
                .andReturn();
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void acceptProposal() throws Exception {

        var proposal = new Proposal();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doReturn( proposal ).when( proposalService ).findProposalFor( (Freelancer) freelancerUser.getType(), 1L );

        mockMvc.perform( post( "/freelancer/proposals/accept/1" )
                .with( csrf() ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

        Mockito.verify( proposalService, Mockito.times( 1 ) ).setStatus( proposal, Status.ACCEPTED );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void acceptProposalInvalidTransition() throws Exception {

        var proposal = new Proposal();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doReturn( proposal ).when( proposalService ).findProposalFor( (Freelancer) freelancerUser.getType(), 1L );
        Mockito.doThrow( InvalidTransitionException.class ).when( proposalService ).setStatus( proposal, Status.ACCEPTED );

        mockMvc.perform( post( "/freelancer/proposals/accept/1" )
                .with( csrf() ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

    }


    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void acceptProposalNotFound() throws Exception {

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doThrow( ProposalNotAllowedException.class ).when( proposalService ).findProposalFor( (Freelancer) freelancerUser.getType(), 1L );

        mockMvc.perform( post( "/freelancer/proposals/accept/1" )
                .with( csrf() ) )
                .andExpect( status().is3xxRedirection() )
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
