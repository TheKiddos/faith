package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.FreelancerService;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class FreelancerTest {
    private final FreelancerService freelancerService;
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    @Autowired
    public FreelancerTest( FreelancerService freelancerService, SkillRepository skillRepository, UserService userService, UserRepository userRepository, ProjectService projectService, ProjectRepository projectRepository ) {
        this.freelancerService = freelancerService;
        this.skillRepository = skillRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void updateFreelancerProfile() {
        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        FreelancerDto dto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        Freelancer freelancer = (Freelancer) user.getType();
        assertNotEquals( dto.getSummary(), freelancer.getSummary() );
        assertNotEquals( dto.isAvailable(), freelancer.isAvailable() );
        assertTrue( freelancer.getSkills().isEmpty() );

        freelancerService.updateProfile( user, dto );

        user = (User) userService.loadUserByUsername( user.getEmail() );
        freelancer = (Freelancer) user.getType();
        assertEquals( dto.getSummary(), freelancer.getSummary() );
        assertEquals( dto.isAvailable(), freelancer.isAvailable() );
        assertEquals( Set.of( Skill.of( "c++" ), Skill.of( "suck" ) ), freelancer.getSkills() );
    }

    @Test
    void updateFreelancerProfileForNonFreelancer() {
        User user = userService.createUser( UserDto.builder()
                .email( "bhbh@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        FreelancerDto dto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( true )
                .skills( "c++\nsuck" )
                .build();

        freelancerService.updateProfile( user, dto );

        user = (User) userService.loadUserByUsername( user.getEmail() );
        assertFalse( user.getType() instanceof Freelancer );
    }

    @Test
    void getAvailableFreelancersDto() {
        User stakeholderUser = userService.createUser( UserDto.builder()
                .email( "stakeholder@gmail.com" )
                .password( "password" )
                .nickname( "bhbhbh" )
                .type( "Stakeholder" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        ProjectDto projectDto = ProjectDto.builder()
                .duration( 2 )
                .allowBidding( true )
                .description( "Asdasd" )
                .name( "asdasd" )
                .preferredBid( 200 )
                .minimumQualification( 200 ).build();

        Project projectToFindFreelancersFor = projectService.createProjectFor( (Stakeholder) stakeholderUser.getType(), projectDto );
        Project otherProject = projectService.createProjectFor( (Stakeholder) stakeholderUser.getType(), projectDto );

        User freelancerNoBid = userService.createUser( UserDto.builder()
                .email( "freelancerNoBid@gmail.com" )
                .password( "password" )
                .nickname( "freelancerNoBid" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        User freelancerWithBid = userService.createUser( UserDto.builder()
                .email( "freelancerWithBid@gmail.com" )
                .password( "password" )
                .nickname( "freelancerWithBid" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        User freelancerBidOtherProject = userService.createUser( UserDto.builder()
                .email( "freelancerOtherBid@gmail.com" )
                .password( "password" )
                .nickname( "freelancerOtherBid" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        User freelancerNotAvailable = userService.createUser( UserDto.builder()
                .email( "freelancerNot@gmail.com" )
                .password( "password" )
                .nickname( "freelancerNot" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        FreelancerDto freelancerUpdateDto = FreelancerDto.builder()
                .summary( "Hehhehe" )
                .available( false )
                .skills( "c++\nsuck" )
                .build();
        freelancerService.updateProfile( freelancerNotAvailable, freelancerUpdateDto );


        // Create Four Freelancers One With Bid One Without and one not available and one with bid to other project
        // Call method
        // Assert It returns list with two freelancers, and one with bidding amount
    }
}
