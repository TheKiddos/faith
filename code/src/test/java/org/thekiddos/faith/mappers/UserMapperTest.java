package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.models.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void userDtoToUser() {
        UserDTO userDTO = UserDTO.builder().email( "test@gmail.com" ).build(); // TODO: add other stuff
        User user = userMapper.userDtoToUser( userDTO );
        assertEquals( userDTO.getEmail(), user.getEmail() );
        // TODO: assert Rest of stuff
    }
}
