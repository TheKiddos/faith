package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.models.Proposal;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.exceptions.*;

@Mapper(componentModel = "spring", uses = { ProjectService.class } )
public abstract class ProposalMapper {
    @Autowired
    protected FreelancerService freelancerService;

    @Mapping( target = "id", ignore = true )
    @Mapping( target = "project", source = "projectId" )
    @Mapping( target = "freelancer", expression = "java( freelancerService.getAvailableFreelancerById( dto.getFreelancerId() ) )")
    public abstract Proposal toEntity( ProposalDto dto ) throws ProjectNotFoundException, FreelancerNotFoundException;

    @Mapping( target = "projectId", source = "project.id" )
    @Mapping( target = "freelancerId", source = "freelancer.id" )
    public abstract ProposalDto toDto( Proposal proposal );
}
