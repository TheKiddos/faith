package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class RegistrationControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    RegistrationControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    void registrationPageLoadsSuccessfully() throws Exception {
        mockMvc.perform( get( "/register" ) ).andExpect( status().isOk() );
    }

    @Test
    void createUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( null )
                .build();

        String civilIdPath = new ClassPathResource("banner.txt").getFile().getAbsolutePath();
        mockMvc.perform(post("/register")
                        .with( csrf() )
                        .param( "email", userDTO.getEmail() )
                        .param( "firstName", userDTO.getFirstName() )
                        .param( "lastName", userDTO.getLastName() )
                        .param( "civilId", civilIdPath )
                        .param( "phoneNumber", userDTO.getPhoneNumber() )
                        .param( "address", userDTO.getAddress() )
                        .param( "type", "freelancer" ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

        Mockito.verify( userService, Mockito.times( 1 ) ).createUser( any( UserDTO.class ) );
    }

    @Test
    void createUserThatAlreadyExists() throws Exception {
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( null )
                .build();

        Mockito.doReturn( Optional.of( userDTO ) ).when( userRepository ).findById( userDTO.getEmail() );
        String civilIdPath = new ClassPathResource("banner.txt").getFile().getAbsolutePath();
        mockMvc.perform(post("/register")
                .with( csrf() )
                .param( "email", userDTO.getEmail() )
                .param( "firstName", userDTO.getFirstName() )
                .param( "lastName", userDTO.getLastName() )
                .param( "civilId", civilIdPath )
                .param( "phoneNumber", userDTO.getPhoneNumber() )
                .param( "address", userDTO.getAddress() )
                .param( "type", "freelancer" ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( userService, Mockito.times( 0 ) ).createUser( any() );
    }
}
