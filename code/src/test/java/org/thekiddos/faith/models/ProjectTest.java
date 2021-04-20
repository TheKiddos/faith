package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

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
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
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
    
    @Test
    void findAll() {
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
        
        projectDto.setName( "Project2" );
        user = userService.createUser( UserDto.builder()
                .email( "ahah@gmail.com" )
                .password( "password" )
                .nickname( "ahahah" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        stakeholder = (Stakeholder) user.getType();

        Project project2 = projectService.createProjectFor( stakeholder, projectDto );
        
        var projects = projectService.findAll();
        assertEquals( 2, projects.size() );
        assertTrue( projects.contains( project ) );
        assertTrue( projects.contains( project2 ) );
    }
    
    @Test
    void findAllDto() {
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

        projectService.createProjectFor( stakeholder, projectDto );
        
        ProjectDto projectDto2 = ProjectDto.builder()
                .name( "new world order2" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();
        
        user = userService.createUser( UserDto.builder()
                .email( "ahah@gmail.com" )
                .password( "password" )
                .nickname( "ahahah" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        stakeholder = (Stakeholder) user.getType();

        projectService.createProjectFor( stakeholder, projectDto2 );
        
        var projects = projectService.findAllDto();
        assertEquals( 2, projects.size() );
        assertTrue( projects.contains( projectDto ) );
        assertTrue( projects.contains( projectDto2 ) );
    }
}
