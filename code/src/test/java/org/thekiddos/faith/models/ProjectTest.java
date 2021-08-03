package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.ProposalRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.ProposalService;
import org.thekiddos.faith.services.UserService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ProjectTest {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProposalService proposalService;
    private final ProposalRepository proposalRepository;

    @Autowired
    ProjectTest( ProjectService projectService, ProjectRepository projectRepository, UserService userService, UserRepository userRepository, ProposalService proposalService, ProposalRepository proposalRepository ) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.proposalService = proposalService;
        this.proposalRepository = proposalRepository;
    }

    @BeforeEach
    public void setUp() {
        proposalRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        proposalRepository.deleteAll();
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
        assertFalse( project.isClosed() );

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
        assertTrue( projects.stream().anyMatch( element -> element.getName().equals( projectDto.getName() ) ) );
        assertTrue( projects.stream().anyMatch( element -> element.getName().equals( projectDto2.getName() ) ) );
    }

    @Test
    void findAllPublicDto() {
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

        User freelancerUser = userService.createUser( UserDto.builder()
                .email( "freelancer@projecttest.com" )
                .password( "password" )
                .nickname( "freelancerproject" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        Freelancer freelancer = (Freelancer) freelancerUser.getType();
        freelancer.setAvailable( true );
        freelancer.setUser( freelancerUser );
        userRepository.save( freelancerUser );

        // Public Project
        var expectedProject = projectService.createProjectFor( stakeholder, projectDto );

        // Public Assigned Project
        var assignedProject = projectService.createProjectFor( stakeholder, projectDto );
        var proposalDto = ProposalDto.builder()
                .projectId( assignedProject.getId() )
                .amount( 200 )
                .freelancerId( freelancer.getId() )
                .build();
        proposalService.sendProposal( proposalDto );

        // Closed Project
        var closedProject = projectService.createProjectFor( stakeholder, projectDto );
        closedProject.setClosed( true );
        projectRepository.save( closedProject );

        // Private Project
        projectDto.setAllowBidding( false );
        projectService.createProjectFor( stakeholder, projectDto );

        var projects = projectService.findAllPublicDto();
        assertEquals( 1, projects.size() );

        var availableProject = projects.get( 0 );
        assertEquals( expectedProject.getId(), availableProject.getId() );
    }

    @Test
    void findPublicProjectById() {
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

        User freelancerUser = userService.createUser( UserDto.builder()
                .email( "freelancer@projecttest.com" )
                .password( "password" )
                .nickname( "freelancerproject" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        Freelancer freelancer = (Freelancer) freelancerUser.getType();
        freelancer.setAvailable( true );
        freelancer.setUser( freelancerUser );
        userRepository.save( freelancerUser );

        // Public Project
        var expectedProject = projectService.createProjectFor( stakeholder, projectDto );

        // Public Assigned Project
        var assignedProject = projectService.createProjectFor( stakeholder, projectDto );
        var proposalDto = ProposalDto.builder()
                .projectId( assignedProject.getId() )
                .amount( 200 )
                .freelancerId( freelancer.getId() )
                .build();
        proposalService.sendProposal( proposalDto );

        // Closed Project
        var closedProject = projectService.createProjectFor( stakeholder, projectDto );
        closedProject.setClosed( true );
        projectRepository.save( closedProject );

        // Private Project
        projectDto.setAllowBidding( false );
        var privateProject = projectService.createProjectFor( stakeholder, projectDto );

        assertEquals( expectedProject, projectService.findPublicProjectById( expectedProject.getId() ) );
        assertThrows( ProjectNotFoundException.class, () -> projectService.findPublicProjectById( assignedProject.getId() ) );
        assertThrows( ProjectNotFoundException.class, () -> projectService.findPublicProjectById( closedProject.getId() ) );
        assertThrows( ProjectNotFoundException.class, () -> projectService.findPublicProjectById( privateProject.getId() ) );
    }

    @Test
    void findFeaturedProjectsDto() {
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

        // We are not going to actually implement featured projects but we have it for future cases
        // So now we assume that it just return like 10 projects (or all of them if there are less)

        // Edge case less than 10
        projectService.createProjectFor( stakeholder, projectDto );
        var projects = projectService.findFeaturedProjectsDto();
        assertEquals( 1, projects.size() );

        int NUMBER_OF_FEATURED_PROJECTS = 10;
        for ( int i = 0; i < NUMBER_OF_FEATURED_PROJECTS + 5; ++i )
            projectService.createProjectFor( stakeholder, projectDto );

        projects = projectService.findFeaturedProjectsDto();
        assertEquals( 10, projects.size() );
        assertTrue( projects.stream().allMatch( element -> element.getName().equals( projectDto.getName() ) ) );
    }

    @Test
    void findFeaturedProjectsDtoNotPublic() {
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

        User freelancerUser = userService.createUser( UserDto.builder()
                .email( "freelancer@projecttest.com" )
                .password( "password" )
                .nickname( "freelancerproject" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        Freelancer freelancer = (Freelancer) freelancerUser.getType();
        freelancer.setAvailable( true );
        freelancer.setUser( freelancerUser );
        userRepository.save( freelancerUser );

        // Public Assigned Project
        var assignedProject = projectService.createProjectFor( stakeholder, projectDto );
        var proposalDto = ProposalDto.builder()
                .projectId( assignedProject.getId() )
                .amount( 200 )
                .freelancerId( freelancer.getId() )
                .build();
        proposalService.sendProposal( proposalDto );

        // Closed Project
        var closedProject = projectService.createProjectFor( stakeholder, projectDto );
        closedProject.setClosed( true );
        projectRepository.save( closedProject );

        // Private Project
        projectDto.setAllowBidding( false );
        projectService.createProjectFor( stakeholder, projectDto );

        var projects = projectService.findFeaturedProjectsDto();
        assertEquals( 0, projects.size() );
    }

    @Test
    void findFeaturedProjectsDtoEmpty() {
        var projects = projectService.findFeaturedProjectsDto();
        assertEquals( 0, projects.size() );
    }

    @Test
    void findByOwnerDto() {
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

        projectService.createProjectFor( stakeholder, projectDto2 );

        ProjectDto projectDto3 = ProjectDto.builder()
                .name( "new world order3" )
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
        Stakeholder stakeholder2 = (Stakeholder) user.getType();

        projectService.createProjectFor( stakeholder2, projectDto3 );

        var projects = projectService.findByOwnerDto( stakeholder );
        assertEquals( 2, projects.size() );
        assertTrue( projects.stream().anyMatch( element -> element.getName().equals( projectDto.getName() ) ) );
        assertTrue( projects.stream().anyMatch( element -> element.getName().equals( projectDto2.getName() ) ) );
    }

    @Test
    void findByIdForOwnerDto() {
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

        var expectedProject = projectService.createProjectFor( stakeholder, projectDto );

        ProjectDto projectDto2 = ProjectDto.builder()
                .name( "new world order2" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        projectService.createProjectFor( stakeholder, projectDto2 );

        ProjectDto projectDto3 = ProjectDto.builder()
                .name( "new world order3" )
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
        Stakeholder stakeholder2 = (Stakeholder) user.getType();

        projectService.createProjectFor( stakeholder2, projectDto3 );

        var actualProject = projectService.findByIdForOwnerDto( stakeholder, expectedProject.getId() );
        assertEquals( expectedProject.getId(), actualProject.getId() );
        assertEquals( expectedProject.getDescription(), actualProject.getDescription() );
        assertEquals( expectedProject.getName(), actualProject.getName() );
    }

    @Test
    void findByIdForOwnerDtoNotFound() {
        assertThrows( ProjectNotFoundException.class, () -> projectService.findByIdForOwnerDto( new Stakeholder(), -1L ) );
    }

    @Test
    void findByIdForOwnerDtoNotFoundDifferentOwner() {
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

        var expectedProject = projectService.createProjectFor( stakeholder, projectDto );

        ProjectDto projectDto2 = ProjectDto.builder()
                .name( "new world order2" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();

        projectService.createProjectFor( stakeholder, projectDto2 );

        ProjectDto projectDto3 = ProjectDto.builder()
                .name( "new world order3" )
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
        Stakeholder stakeholder2 = (Stakeholder) user.getType();

        projectService.createProjectFor( stakeholder2, projectDto3 );

        assertThrows( ProjectNotFoundException.class, () -> projectService.findByIdForOwnerDto( stakeholder2, expectedProject.getId() ) );
    }

    @Test
    void closeProject() {
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

        assertFalse( project.isClosed() );

        projectService.closeProject( project );

        project = projectService.findById( project.getId() );

        assertTrue( project.isClosed() );
    }

    @Test
    void closeProjectTwice() {
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
        projectService.closeProject( project );
        project = projectService.findById( project.getId() );

        projectService.closeProject( project );
        project = projectService.findById( project.getId() );
        assertTrue( project.isClosed() );
    }
}
