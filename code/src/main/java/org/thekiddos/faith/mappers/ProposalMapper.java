package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.exceptions.FreelancerNotFoundException;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.models.Proposal;
import org.thekiddos.faith.services.FreelancerService;
import org.thekiddos.faith.services.ProjectService;

@Mapper(componentModel = "spring")
public abstract class ProposalMapper {
    @Autowired
    protected FreelancerService freelancerService;
    @Autowired
    protected ProjectService projectService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "project", expression = "java( projectService.findById( dto.getProjectId() ) )")
    @Mapping(target = "freelancer", expression = "java( freelancerService.getAvailableFreelancerById( dto.getFreelancerId() ) )")
    public abstract Proposal toEntity( ProposalDto dto ) throws ProjectNotFoundException, FreelancerNotFoundException;

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "freelancerId", source = "freelancer.id")
    @Mapping(target = "status", expression = "java(proposal.getStatus().name())")
    public abstract ProposalDto toDto( Proposal proposal );
}
