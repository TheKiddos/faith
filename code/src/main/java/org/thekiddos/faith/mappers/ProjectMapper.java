package org.thekiddos.faith.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.models.Project;


@Mapper
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper( ProjectMapper.class );

    @Mapping( target = "owner", ignore = true )
    @Mapping( target = "closed", ignore = true )
    @Mapping( target = "id", ignore = true )
    @Mapping( target = "duration", expression = "java( java.time.Duration.ofDays( projectDto.getDuration() ) )" )
    Project projectDtoToProject( ProjectDto projectDto );
    
    @Mapping( target = "duration", expression = "java( (int)project.getDuration().toDays() )" )
    ProjectDto projectToProjectDto( Project project );
}
