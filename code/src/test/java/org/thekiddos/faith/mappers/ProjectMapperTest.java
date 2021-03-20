package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.Test;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.models.Project;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {
    private final ProjectMapper projectMapper = ProjectMapper.INSTANCE;

    @Test
    void projectDtoToProject() {
        ProjectDto projectDto = ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        Project project = projectMapper.projectDtoToProject( projectDto );

        assertNotNull( project );
        assertEquals( projectDto.getName(), project.getName() );
        assertEquals( projectDto.getDescription(), project.getDescription() );
        assertEquals( projectDto.getPreferredBid(), project.getPreferredBid() );
        assertEquals( Duration.ofDays( projectDto.getDuration() ), project.getDuration() );
        assertEquals( projectDto.getMinimumQualification(), project.getMinimumQualification() );
        assertEquals( projectDto.isAllowBidding(), project.isAllowBidding() );
    }

    @Test
    void nullTest() {
        assertNull( projectMapper.projectDtoToProject( null ) );
    }
}
