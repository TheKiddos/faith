package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.FreelancerNotFoundException;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.SkillRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.*;

import java.util.List;
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
    private final BidService bidService;
    private final BidRepository bidRepository;

    @MockBean
    private FreelancerRatingService freelancerRatingService;

    @Autowired
    public FreelancerTest( FreelancerService freelancerService, SkillRepository skillRepository, UserService userService, UserRepository userRepository, ProjectService projectService, ProjectRepository projectRepository, BidService bidService, BidRepository bidRepository ) {
        this.freelancerService = freelancerService;
        this.skillRepository = skillRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.bidService = bidService;
        this.bidRepository = bidRepository;
    }

    @BeforeEach
    void setUp() {
        bidRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        bidRepository.deleteAll();
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
        FreelancerDto freelancerUpdateDto = FreelancerDto.builder().available( true ).build();
        freelancerService.updateProfile( freelancerNoBid, freelancerUpdateDto );

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

        User freelancerDisabled = userService.createUser( UserDto.builder()
                .email( "freelancer@disabled.com" )
                .password( "password" )
                .nickname( "freelancerDisabled" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        freelancerDisabled.setEnabled( false );
        userRepository.save( freelancerDisabled );

        BidDto bidDto = BidDto.builder()
                .amount( 100 )
                .projectId( projectToFindFreelancersFor.getId() )
                .build();
        bidService.addBid( bidDto, (Freelancer) freelancerWithBid.getType() );

        bidDto.setProjectId( otherProject.getId() );
        bidService.addBid( bidDto, (Freelancer) freelancerBidOtherProject.getType() );

        List<FreelancerDto> freelancers = freelancerService.getAvailableFreelancersDto( projectToFindFreelancersFor );
        assertEquals( 2, freelancers.size() );
        assertTrue( freelancers.stream().anyMatch( freelancerDto -> freelancerDto.getProjectBidAmount() == 100 ) );
        assertTrue( freelancers.stream().anyMatch( freelancerDto -> freelancerDto.getProjectBidAmount() == 0 ) );
    }

    @Test
    void findFeaturedFreelancersDto() {
        // TODO: Should limit number of freelancer to get

        User freelancer = userService.createUser( UserDto.builder()
                .email( "freelancer@gmail.com" )
                .password( "password" )
                .nickname( "freelancer" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        User freelancer2 = userService.createUser( UserDto.builder()
                .email( "freelancer2@gmail.com" )
                .password( "password" )
                .nickname( "freelancer2" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        User freelancer3 = userService.createUser( UserDto.builder()
                .email( "freelancer3@gmail.com" )
                .password( "password" )
                .nickname( "freelancer3" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );

        Mockito.doReturn( 2.0 ).when( freelancerRatingService ).getRating( (Freelancer) freelancer.getType() );
        Mockito.doReturn( 5.0 ).when( freelancerRatingService ).getRating( (Freelancer) freelancer2.getType() );
        Mockito.doReturn( 3.0 ).when( freelancerRatingService ).getRating( (Freelancer) freelancer3.getType() );

        List<FreelancerDto> freelancers = freelancerService.findFeaturedFreelancersDto();
        assertEquals( 3, freelancers.size() );
        assertEquals( 5.0, freelancers.get( 0 ).getRating() );
        assertEquals( 3.0, freelancers.get( 1 ).getRating() );
        assertEquals( 2.0, freelancers.get( 2 ).getRating() );
    }

    @Test
    void findFeaturedFreelancersDtoDisabledFreelancer() {
        // TODO: Should limit number of freelancer to get

        User freelancer = userService.createUser( UserDto.builder()
                .email( "freelancer@gmail.com" )
                .password( "password" )
                .nickname( "freelancer" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        freelancer.setEnabled( false );
        userRepository.save( freelancer );

        Mockito.doReturn( 2.0 ).when( freelancerRatingService ).getRating( (Freelancer) freelancer.getType() );

        List<FreelancerDto> freelancers = freelancerService.findFeaturedFreelancersDto();
        assertEquals( 0, freelancers.size() );
    }

    @Test
    void getAvailableFreelancerById() {
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
        FreelancerDto freelancerUpdateDto = FreelancerDto.builder().available( true ).build();
        freelancerService.updateProfile( freelancerNoBid, freelancerUpdateDto );

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

        User freelancerDisabled = userService.createUser( UserDto.builder()
                .email( "freelancer@disabled.com" )
                .password( "password" )
                .nickname( "freelancerDisabled" )
                .type( "Freelancer" )
                .firstName( "Test" )
                .lastName( "aaa" )
                .build() );
        freelancerDisabled.setEnabled( false );
        userRepository.save( freelancerDisabled );

        BidDto bidDto = BidDto.builder()
                .amount( 100 )
                .projectId( projectToFindFreelancersFor.getId() )
                .build();
        bidService.addBid( bidDto, (Freelancer) freelancerWithBid.getType() );

        bidDto.setProjectId( otherProject.getId() );
        bidService.addBid( bidDto, (Freelancer) freelancerBidOtherProject.getType() );

        assertEquals( freelancerNoBid.getType(), freelancerService.getAvailableFreelancerById( freelancerNoBid.getType().getId() ) );

        assertThrows( FreelancerNotFoundException.class, () -> freelancerService.getAvailableFreelancerById( freelancerWithBid.getType().getId() ) );
        assertThrows( FreelancerNotFoundException.class, () -> freelancerService.getAvailableFreelancerById( freelancerBidOtherProject.getType().getId() ) );
        assertThrows( FreelancerNotFoundException.class, () -> freelancerService.getAvailableFreelancerById( freelancerNotAvailable.getType().getId() ) );
        assertThrows( FreelancerNotFoundException.class, () -> freelancerService.getAvailableFreelancerById( freelancerDisabled.getType().getId() ) );
    }
}
