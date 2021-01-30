package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.models.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void userDtoToUser() {
        String password = "password";
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" )
                                           .password( password )
                                           .firstName( "Test" )
                                           .lastName( "User" )
                                           .civilId( new byte[]{} )
                                           .phoneNumber( "+963987654321" )
                                           .address( "Street" )
                                           .type( null )
                                           .build();

        assertEquals( password, userDTO.getPassword() );

        User user = userMapper.userDtoToUser( userDTO );
        assertEquals( userDTO.getEmail(), user.getEmail() );
        assertNotEquals( password, user.getPassword() );
        assertTrue( user.checkPassword( "password" ) );
        assertEquals( userDTO.getFirstName(), user.getFirstName() );
        assertEquals( userDTO.getLastName(), user.getLastName() );
        assertArrayEquals( userDTO.getCivilId(), user.getCivilId() );
        assertEquals( userDTO.getAddress(), user.getAddress() );
        assertNull( user.getType() );
    }
}
