package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith( SpringExtension.class )
class ProjectTest {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    ProjectTest( ProjectService projectService, ProjectRepository projectRepository, UserService userService, UserRepository userRepository ) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        projectRepository.deleteAll();
        try {
            userRepository.deleteById( "bhbh@gmail.com" );
        }
        catch ( EmptyResultDataAccessException e ) {
            // ignore
        }
    }

    @AfterEach
    void tearDown() {
        try {
            userRepository.deleteById( "bhbh@gmail.com" );
        }
        catch ( EmptyResultDataAccessException e ) {
            // ignore
        }
    }

    @Test
    void createProjectFor() {
        ProjectDto projectDto = ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        Stakeholder stakeholder = (Stakeholder) user.getType();

        Project project = projectService.createProjectFor( stakeholder, projectDto );

        assertNotNull( project );
        assertEquals( projectDto.getName(), project.getName() );
        assertEquals( projectDto.getDescription(), project.getDescription() );
        assertEquals( projectDto.getPreferredBid(), project.getPreferredBid() );
        assertEquals( Duration.ofDays( projectDto.getDuration() ), project.getDuration() );
        assertEquals( projectDto.getMinimumQualification(), project.getMinimumQualification() );
        assertEquals( projectDto.isAllowBidding(), project.isAllowBidding() );
        assertEquals( stakeholder, project.getOwner() );

        project = projectRepository.findById( project.getId() ).orElse( null );
        assertNotNull( project );
        assertEquals( projectDto.getName(), project.getName() );
        assertEquals( projectDto.getDescription(), project.getDescription() );
        assertEquals( projectDto.getPreferredBid(), project.getPreferredBid() );
        assertEquals( Duration.ofDays( projectDto.getDuration() ), project.getDuration() );
        assertEquals( projectDto.getMinimumQualification(), project.getMinimumQualification() );
        assertEquals( projectDto.isAllowBidding(), project.isAllowBidding() );
    }
}
