package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.models.UserType;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    User userDtoToUser( UserDto userDTO );

    @Mapping( target = "passwordConfirm", ignore = true )
    @Mapping( target = "password", ignore = true )
    @Mapping( target = "civilId", ignore = true )
    @Mapping( target = "type", ignore = true )
    UserDto userToUserDto( User user );

    default UserType UserTypeDtoToUserType( String userType ) {
        if ( userType == null )
            return null;

        return switch ( userType ) {
            case "Stakeholder" -> new Stakeholder();
            case "Freelancer" -> new Freelancer();
            default -> null;
        };
    }
}
