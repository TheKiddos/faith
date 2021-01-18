package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.thekiddos.faith.dtos.UserDTO;
import org.thekiddos.faith.models.User;

@Mapper
@Service
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userDtoToUser( UserDTO userDTO );
}
