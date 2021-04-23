package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.models.BidComment;
import org.thekiddos.faith.services.BidService;

@Mapper(componentModel = "spring", uses = { BidService.class } )
public interface BidCommentMapper {
    @Mapping( target = "id", ignore = true )
    @Mapping( target = "bid", source = "bidId" )
    BidComment toEntity( BidCommentDto dto );

    @Mapping( target = "bidId", source = "bid.id" )
    BidCommentDto toDto( BidComment entity );
}
