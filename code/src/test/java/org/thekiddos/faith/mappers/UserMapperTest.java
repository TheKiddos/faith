package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.models.UserType;

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
    void userDtoToStakeholderUser() {
        String password = "password";
        UserDto userDto = UserDto.builder().email( "test@gmail.com" )
                .password( password )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Stakeholder" )
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

        UserType userType = user.getType();
        assertNotNull( userType );
        assertEquals( "Stakeholder", userType.toString() );
        Stakeholder stakeholder = (Stakeholder) userType;
        assertNotNull( stakeholder );
    }

    @Test
    void userDtoToFreelancerUser() {
        String password = "password";
        UserDto userDto = UserDto.builder().email( "test@gmail.com" )
                .password( password )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Freelancer" )
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

        UserType userType = user.getType();
        assertNotNull( userType );
        assertEquals( "Freelancer", userType.toString() );
        Freelancer freelancer = (Freelancer) userType;
        assertNotNull( freelancer );
    }

    @Test
    void nullTest() {
        assertNull( userMapper.userDtoToUser( null ) );
    }

    @Test
    void nullTestToDto() {
        assertNull( userMapper.userToUserDto( null ) );
    }

    @Test
    void userToUserDto() {
        User user = new User();
        user.setNickname( "death" );
        user.setEmail( "hello@gmail.com" );
        user.setFirstName( "Death" );
        user.setLastName( "RIP" );
        user.setPhoneNumber( "+963912345678" );
        user.setAddress( "hellstreet" );

        UserDto userDto = userMapper.userToUserDto( user );

        assertEquals( user.getEmail(), userDto.getEmail() );
        assertEquals( user.getNickname(), userDto.getNickname() );
        assertEquals( user.getFirstName(), userDto.getFirstName() );
        assertEquals( user.getLastName(), userDto.getLastName() );
        assertEquals( user.getAddress(), userDto.getAddress() );
        assertEquals( user.getPhoneNumber(), userDto.getPhoneNumber() );
    }
}
