package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProposalRepository;
import org.thekiddos.faith.services.FreelancerRatingService;

@Mapper( componentModel = "spring" )
public abstract class FreelancerMapper {
    @Autowired
    protected BidRepository bidRepository;
    @Autowired
    protected ProposalRepository proposalRepository;
    @Autowired
    protected FreelancerRatingService freelancerRatingService;


    @Mapping( target = "user", ignore = true )
    @Mapping( target = "id", ignore = true )
    @Mapping( target = "skills", expression = "java( org.thekiddos.faith.models.Skill.createSkills( dto.getSkills() ) )" )
    public abstract Freelancer toEntity( FreelancerDto dto );

    public FreelancerDto toDto( Freelancer freelancer ) {
        if ( freelancer == null )
            return null;

        StringBuilder skillsBuffer = new StringBuilder();
        freelancer.getSkills().forEach( skill -> skillsBuffer.append( skill.getName() ).append( "\n" ) );
        String skills = skillsBuffer.length() > 0 ? skillsBuffer.substring( 0, skillsBuffer.length() - 1 ) : "";
        UserDto user = UserMapper.INSTANCE.userToUserDto( freelancer.getUser() );

        return FreelancerDto.builder()
                .id( freelancer.getId() )
                .summary( freelancer.getSummary() )
                .available( freelancer.isAvailable() )
                .skills( skills )
                .user( user )
                .rating( freelancerRatingService.getRating( freelancer ) )
                .build();
    }

    public FreelancerDto toDtoWithProject( Freelancer freelancer, Project projectToFindFreelancersFor ) {
        FreelancerDto dto = toDto( freelancer );

        if ( projectToFindFreelancersFor != null ) {
            var bid = bidRepository.findByBidderAndProject( freelancer, projectToFindFreelancersFor );
            var proposal = proposalRepository.findByProjectAndFreelancer( projectToFindFreelancersFor, freelancer );
            bid.ifPresent( value -> dto.setProjectBidAmount( value.getAmount() ) );
            proposal.ifPresent( value -> dto.setProjectProposalAmount( value.getAmount() ) );
        }

        return dto;
    }
}
