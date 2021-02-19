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
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RegistrationControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    private UserMapper userMapper = UserMapper.INSTANCE;

    @Autowired
    RegistrationControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    void registrationPageLoadsSuccessfully() throws Exception {
        mockMvc.perform( get( "/register" ) ).andExpect( status().isOk() );
    }

    @Test
    void registrationSuccessfulPageLoadsSuccessfully() throws Exception {
        mockMvc.perform( get( "/register/success" ) ).andExpect( status().isOk() );
    }

    @Test
    void createUser() throws Exception {
        String password = "password";
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" )
                .password( password )
                .passwordConfirm( password )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( null )
                .build();

        User user = userMapper.userDtoToUser( userDTO );
        Mockito.doReturn( user ).when( userService ).createUser( any( UserDTO.class ) );

        String civilIdPath = new ClassPathResource("banner.txt").getFile().getAbsolutePath();
        mockMvc.perform(post("/register")
                        .with( csrf() )
                        .param( "email", userDTO.getEmail() )
                        .param( "password", password )
                        .param( "passwordConfirm", password )
                        .param( "nickname", userDTO.getNickname() )
                        .param( "firstName", userDTO.getFirstName() )
                        .param( "lastName", userDTO.getLastName() )
                        .param( "civilId", civilIdPath )
                        .param( "phoneNumber", userDTO.getPhoneNumber() )
                        .param( "address", userDTO.getAddress() )
                        .param( "type", "freelancer" ) )
                .andExpect( status().is3xxRedirection() )
                .andReturn();

        Mockito.verify( userService, Mockito.times( 1 ) ).createUser( any( UserDTO.class ) );
        Mockito.verify( userService, Mockito.times( 1 ) ).requireAdminApprovalFor( any( User.class ) );
    }

    @Test
    void createUserThatAlreadyExists() throws Exception {
        String password = "password";
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" )
                .password( password )
                .passwordConfirm( password )
                .nickname( "tasty" )
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
                .param( "password", password )
                .param( "passwordConfirm", password )
                .param( "nickname", userDTO.getNickname() )
                .param( "firstName", userDTO.getFirstName() )
                .param( "lastName", userDTO.getLastName() )
                .param( "civilId", civilIdPath )
                .param( "phoneNumber", userDTO.getPhoneNumber() )
                .param( "address", userDTO.getAddress() )
                .param( "type", "freelancer" ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( userService, Mockito.times( 0 ) ).createUser( any() );
        Mockito.verify( userService, Mockito.times( 0 ) ).requireAdminApprovalFor( any() );
    }

    @Test
    void createUserThatAlreadyExistsForNickName() throws Exception {
        String password = "password";
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" )
                .password( password )
                .passwordConfirm( password )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( null )
                .build();

        Mockito.doReturn( Optional.of( userMapper.userDtoToUser( userDTO ) ) ).when( userRepository ).findByNickname( userDTO.getNickname() );
        String civilIdPath = new ClassPathResource("banner.txt").getFile().getAbsolutePath();
        mockMvc.perform(post("/register")
                .with( csrf() )
                .param( "email", "otheruser@test.com" )
                .param( "password", password )
                .param( "passwordConfirm", password )
                .param( "nickname", userDTO.getNickname() )
                .param( "firstName", userDTO.getFirstName() )
                .param( "lastName", userDTO.getLastName() )
                .param( "civilId", civilIdPath )
                .param( "phoneNumber", userDTO.getPhoneNumber() )
                .param( "address", userDTO.getAddress() )
                .param( "type", "freelancer" ) )
                .andExpect( status().isOk() )
                .andReturn();

        Mockito.verify( userService, Mockito.times( 0 ) ).createUser( any() );
        Mockito.verify( userService, Mockito.times( 0 ) ).requireAdminApprovalFor( any() );
    }
}
