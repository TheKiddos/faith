package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.services.ProjectService;

@Mapper(componentModel = "spring", uses = { ProjectService.class } )
public interface BidMapper {
    @Mapping( target = "bidder", ignore = true )
    @Mapping( target = "id", ignore = true )
    @Mapping( target = "project", source = "projectId" )
    Bid toEntity( BidDto dto );

    @Mapping( target = "projectId", source = "project.id" )
    @Mapping( target = "comment", ignore = true )
    @Mapping( target = "bidComments", ignore = true )
    BidDto toDto( Bid bid );
}
