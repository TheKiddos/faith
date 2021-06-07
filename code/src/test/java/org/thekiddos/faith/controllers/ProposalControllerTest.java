package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.ProposalService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Autowired
    ProposalControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
        this.stakeholderUser = getTestUser();
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
