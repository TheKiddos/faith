package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.models.Freelancer;

@Mapper
public interface FreelancerMapper {
    FreelancerMapper INSTANCE = Mappers.getMapper( FreelancerMapper.class );

    @Mapping( target = "user", ignore = true )
    @Mapping( target = "id", ignore = true )
    @Mapping( target = "skills", expression = "java( org.thekiddos.faith.models.Skill.createSkills( dto.getSkills() ) )" )
    Freelancer toEntity( FreelancerDto dto );

    default FreelancerDto toDto( Freelancer freelancer ) {
        if ( freelancer == null )
            return null;

        StringBuilder skillsBuffer = new StringBuilder();
        freelancer.getSkills().forEach( skill -> skillsBuffer.append( skill.getName() ).append( "\n" ) );
        String skills = skillsBuffer.length() > 0 ? skillsBuffer.substring( 0, skillsBuffer.length() - 1 ) : "";

        return FreelancerDto.builder()
                .summary( freelancer.getSummary() )
                .available( freelancer.isAvailable() )
                .skills( skills )
                .build();
    }
}
