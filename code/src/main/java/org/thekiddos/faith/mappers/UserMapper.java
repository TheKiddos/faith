package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.models.UserType;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    User userDtoToUser( UserDto userDTO );

    default UserType UserTypeDtoToUserType( String userType ) {
        return null;
    }
}
