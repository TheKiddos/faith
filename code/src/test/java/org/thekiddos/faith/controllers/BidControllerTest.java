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
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.BiddingNotAllowedException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.BidService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BidControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private BidService bidService;
    @MockBean
    private UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    private User freelancerUser;

    @Autowired
    BidControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
        this.freelancerUser = getTestUser();
    }

    @Test
    @WithMockUser( authorities = {"USER"})
    void addBidNotFreelancer() throws Exception {
        double amount = 10.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( 1L ) // Don't really matter now since we are mocking
                .build();

        mockMvc.perform( post( "/freelancer/bid" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "comment", dto.getComment() )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isForbidden() )
                .andReturn();

        Mockito.verify( bidService, Mockito.times( 0 ) ).addBid( any(), any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void addBid() throws Exception {
        double amount = 10.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( 1L ) // Don't really matter now since we are mocking
                .build();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );

        mockMvc.perform( post( "/freelancer/bid" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "comment", dto.getComment() )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isCreated() )
                .andReturn();

        Mockito.verify( bidService, Mockito.times( 1 ) ).addBid( dto, (Freelancer)freelancerUser.getType() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void addBidInvalid() throws Exception {
        double amount = -100.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( 1L ) // Don't really matter now since we are mocking
                .build();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );

        mockMvc.perform( post( "/freelancer/bid" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "comment", dto.getComment() )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isBadRequest() )
                .andReturn();

        Mockito.verify( bidService, Mockito.times( 0 ) ).addBid( dto, (Freelancer)freelancerUser.getType() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void addBidInvalidProject() throws Exception {
        double amount = 100.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( -1L ) // Don't really matter now since we are mocking
                .build();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doThrow( ProjectNotFoundException.class ).when( bidService ).addBid( dto, (Freelancer)freelancerUser.getType() );

        mockMvc.perform( post( "/freelancer/bid" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "comment", dto.getComment() )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isBadRequest() )
                .andReturn();
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void addBidInvalidBid() throws Exception {
        double amount = 100.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( 1L ) // Don't really matter now since we are mocking
                .build();

        Mockito.doReturn( Optional.of( freelancerUser ) ).when( userRepository ).findById( freelancerUser.getEmail() );
        Mockito.doThrow( BiddingNotAllowedException.class ).when( bidService ).addBid( dto, (Freelancer)freelancerUser.getType() );

        mockMvc.perform( post( "/freelancer/bid" )
                .with( csrf() )
                .param( "amount", String.valueOf( dto.getAmount() ) )
                .param( "comment", dto.getComment() )
                .param( "projectId", String.valueOf( dto.getProjectId() ) ) )
                .andExpect( status().isBadRequest() )
                .andReturn();
    }

    private User getTestUser() {
        UserDto userDto = UserDto.builder().email( "freelancer@test.com" )
                .password( "password" )
                .passwordConfirm( "password" )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Freelancer" )
                .build();

        return userMapper.userDtoToUser( userDto );
    }
}
