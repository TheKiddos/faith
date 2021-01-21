package org.thekiddos.faith.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.exceptions.UserAlreadyExistException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith( MockitoExtension.class )
class UserTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void createUser() {
        UserDTO userDTO = UserDTO.builder().email( "test@test.com" ).build();
        User user = userMapper.userDtoToUser( userDTO );

        Mockito.doReturn( Optional.empty() ).when( userRepository ).findById( anyString() );
        Mockito.doReturn( user ).when( userRepository ).save( any( User.class ) );

        user = userService.createUser( userDTO );

        Mockito.doReturn( List.of( user ) ).when( userRepository ).findAll();

        assertEquals( 1, userService.getAll().size() );
        assertTrue( userService.getAll().contains( user ) );
    }

    @Test
    void uniqueEmail() {
        UserDTO userDTO = UserDTO.builder().email( "test@test.com" ).build();
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
        user.setEmail( "abc@gmail2.com" );

        assertNotEquals( user, otherUser );
        assertNotEquals( user.hashCode(), otherUser.hashCode() );
    }

}
