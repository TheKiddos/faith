package org.thekiddos.faith.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.EmailService;
import org.thekiddos.faith.services.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith( MockitoExtension.class )
class UserTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserServiceImpl userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void createUser() {
        UserDTO userDTO = UserDTO.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findById( anyString() );

        assertThrows( UsernameNotFoundException.class, () -> userService.loadUserByUsername( "test@test.com" ) );

        Mockito.doReturn( user ).when( userRepository ).save( any( User.class ) );

        user = userService.createUser( userDTO );

        Mockito.doReturn( List.of( user ) ).when( userRepository ).findAll();

        assertEquals( 1, userService.getAll().size() );
        assertTrue( userService.getAll().contains( user ) );

        assertFalse( user.isAdmin() );
        assertEquals( Collections.singletonList( new SimpleGrantedAuthority( "USER" ) ), user.getAuthorities() );
    }

    @Test
    void userAccountManagement() {
        UserDTO userDTO = UserDTO.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        assertEquals( user.getEmail(), user.getUsername() );

        assertTrue( user.isEnabled() );
        user.setEnabled( false );
        assertFalse( user.isEnabled() );

        assertTrue( user.isAccountNonExpired() );
        assertTrue( user.isCredentialsNonExpired() );
        assertTrue( user.isAccountNonLocked() );
        assertFalse( user.isAdmin() );
        assertEquals( Collections.singletonList( new SimpleGrantedAuthority( "USER" ) ), user.getAuthorities() );
    }

    @Test
    void requireAdminApprovalFor() {
        UserDTO userDTO = UserDTO.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );
        assertTrue( user.isEnabled() );

        UserDTO adminDTO = UserDTO.builder().email( "admin@test.com" ).password( "password" ).build();
        User admin = userMapper.userDtoToUser( adminDTO );

        Mockito.doReturn( List.of( admin ) ).when( userRepository ).findByAdminTrue();
        Mockito.doReturn( null ).when( userRepository ).save( any() );
        userService.requireAdminApprovalFor( user );
        assertFalse( user.isEnabled() );
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( admin.getEmail() ) ), anyString(), anyString(), anyString(), any() );
    }

    @Test
    void uniqueEmail() {
        UserDTO userDTO = UserDTO.builder().email( "test@test.com" ).password( "password" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findById( anyString() );
        Mockito.doReturn( user ).when( userRepository ).save( any( User.class ) );

        user = userService.createUser( userDTO );

        Mockito.doReturn(  Optional.of( user ) ).when( userRepository ).findById( anyString() );

        assertThrows( UserAlreadyExistException.class , () -> userService.createUser( userDTO ) );

        Mockito.doReturn( List.of( user ) ).when( userRepository ).findAll();
        assertEquals( 1, userService.getAll().size() );
        assertTrue( userService.getAll().contains( user ) );
    }

    @Test
    void equalsAndHashcode() {
        User user = new User();
        user.setEmail( "abc@gmail.com" );

        User otherUser = new User();
        otherUser.setEmail( "abc@gmail2.com" );

        assertNotEquals( user, otherUser );
        assertEquals( user, user );
        assertNotEquals( user, null );
        assertNotEquals( user.hashCode(), otherUser.hashCode() );

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail( "abc@gmail.com" );

        UserDTO otherUserDTO = new UserDTO();
        otherUserDTO.setEmail( "abc@gmail2.com" );

        assertNotEquals( userDTO, otherUserDTO );
        assertEquals( userDTO, userDTO );
        assertNotEquals( userDTO, null );
        assertNotEquals( userDTO.hashCode(), otherUserDTO.hashCode() );
    }

}
