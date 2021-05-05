package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.models.BidComment;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.UserService;

@Mapper(componentModel = "spring", uses = { BidService.class } )
public abstract class BidCommentMapper {
    @Autowired
    protected UserService userService;
    protected final UserMapper userMapper = UserMapper.INSTANCE;

    @Mapping( target = "id", ignore = true )
    @Mapping( target = "bid", source = "bidId" )
    @Mapping( target = "user", expression = "java( userService.findByEmail( dto.getUser().getEmail() ) )")
    public abstract BidComment toEntity( BidCommentDto dto );

    @Mapping( target = "bidId", source = "bid.id" )
    @Mapping( target = "user", expression = "java( userMapper.userToUserDto( entity.getUser() ) )")
    public abstract BidCommentDto toDto( BidComment entity );
}
