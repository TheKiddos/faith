package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void userDtoToUser() {
        String password = "password";
        UserDto userDto = UserDto.builder().email( "test@gmail.com" )
                                           .password( password )
                                           .nickname( "tasty" )
                                           .firstName( "Test" )
                                           .lastName( "User" )
                                           .civilId( new byte[]{} )
                                           .phoneNumber( "+963987654321" )
                                           .address( "Street" )
                                           .type( null )
                                           .build();

        assertEquals( password, userDto.getPassword() );
        User user = userMapper.userDtoToUser( userDto );
        assertEquals( userDto.getEmail(), user.getEmail() );
        assertNotEquals( password, user.getPassword() );
        assertTrue( user.checkPassword( "password" ) );
        assertEquals( userDto.getNickname(), user.getNickname() );
        assertEquals( userDto.getFirstName(), user.getFirstName() );
        assertEquals( userDto.getLastName(), user.getLastName() );
        assertArrayEquals( userDto.getCivilId(), user.getCivilId() );
        assertEquals( userDto.getAddress(), user.getAddress() );
        assertNull( user.getType() );
    }

    @Test
    void nullTest() {
        assertNull( userMapper.userDtoToUser( null ) );
    }
}
