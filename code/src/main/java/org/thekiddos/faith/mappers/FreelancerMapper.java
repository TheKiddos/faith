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
    @Mapping( target = "skills", expression = "java( new java.util.HashSet<>( org.thekiddos.faith.models.Skill.createSkills( dto.getSkills() ) ) )" )
    Freelancer toEntity( FreelancerDto dto );
}