package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.services.ProjectService;

@Mapper(componentModel = "spring")
public abstract class BidMapper {
    @Autowired
    protected FreelancerMapper freelancerMapper;
    @Autowired
    protected ProjectService projectService;

    @Mapping(target = "bidder", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", expression = "java( projectService.findById( dto.getProjectId() ) )")
    public abstract Bid toEntity( BidDto dto );

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "bidComments", ignore = true)
    @Mapping(target = "bidder", expression = "java( freelancerMapper.toDto( bid.getBidder() ) )")
    public abstract BidDto toDto( Bid bid );
}
