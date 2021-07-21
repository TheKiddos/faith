package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.FreelancerRatingService;
import org.thekiddos.faith.services.FreelancerService;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FreelancerControllerTest {
    private final MockMvc mockMvc;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FreelancerService freelancerService;
    @MockBean
    private FreelancerRatingService freelancerRatingService;

    @Autowired
    FreelancerControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser( authorities = { "USER", "FREELANCER" }, username = "freelancer@test.com" )
    void profilePageLoadsOk() throws Exception {
        User user = getTestUser();
        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );

        Mockito.doReturn( 0.0 ).when( freelancerRatingService ).getRating( (Freelancer) user.getType() );
        mockMvc.perform( get( "/freelancer/profile" ) )
                .andExpect( status().isOk() );

        Mockito.verify( userRepository, Mockito.times( 1 ) ).findById( user.getEmail() );
    }

    @Test
    @WithMockUser( authorities = {"USER"} )
    void profilePageRequiresFreelancer() throws Exception {
        mockMvc.perform( get(  "/freelancer/profile" ) )
                .andExpect( status().isForbidden() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void updateProfile() throws Exception {
        FreelancerDto dto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        User user = getTestUser();
        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );

        mockMvc.perform(post( "/freelancer/profile" )
                .with( csrf() )
                .param( "summary", dto.getSummary() )
                .param( "available", String.valueOf( dto.isAvailable() ) )
                .param( "skills", dto.getSkills() ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

        Mockito.verify( freelancerService, Mockito.times( 1 ) ).updateProfile( user, dto );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com")
    void updateProfileInvalid() throws Exception {
        FreelancerDto dto = FreelancerDto.builder()
                .summary( null )
                .available( true )
                .skills( null )
                .build();

        User user = getTestUser();
        Mockito.doReturn( Optional.of( user ) ).when( userRepository ).findById( user.getEmail() );

        mockMvc.perform(post( "/freelancer/profile" )
                .with( csrf() )
                .param( "summary", dto.getSummary() )
                .param( "available", String.valueOf( dto.isAvailable() ) )
                .param( "skills", dto.getSkills() ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( freelancerService, Mockito.times( 0 ) ).updateProfile( user, dto );
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
