package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.models.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    User userDtoToUser( UserDTO userDTO );
}
