package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.services.*;
import org.thekiddos.faith.dtos.*;
import org.thekiddos.faith.mappers.*;
import org.thekiddos.faith.repositories.*;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class BidTest {
    private final BidService bidService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectService projectService;
    
    @MockBean
    private EmailService emailService;
    
    private Project project;
    private User freelancerUser;
    
    @Autowired
    public BidTest( BidService bidService, 
                    UserRepository userRepository, 
                    ProjectRepository projectRepository
                    UserService userService
                    ProjectService projectService ) {
        this.bidService = bidService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
    }
    
    @AfterEach
    public void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @BeforeEach
    public void setUp() {
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

        this.project = projectService.createProjectFor( stakeholder, projectDto );
        this.freelancerUser = getTestUser();
    }
    
    /**
     * Test adding a bid without a comment
     */
    @Test
    void addBid() {
        double amount = 10.0;
        
        assertTrue( ((Freelancer)this.freelancerUser.getType()).getBids().isEmpty() );
        
        bidService.addBid( amount, this.project, (Freelancer)this.freelancerUser.getType() );
        
        var user = userService.loadUserByUsername( this.freelancerUser.getEmail() );
        var bids = ((Freelancer)user.getType()).getBids();
        assertEquals( 1, bids.size() );
        
        Bid bid = bids.stream().findFirst().get();
        assertNotNull( bid.getId() );
        assertEquals( "Bid of " + amount, bid.toString() );
        assertEquals( amount, bid.getAmount() );
        assertEquals( this.project, amount.getProject() );
        assertEquals( (Freelancer)user.getType(), bid.getBidder() );
        
        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }
    
    // Test to make sure only freelancer can bid
    // Test bid with a comment (dto)
    // Test bid not allowed on private projects
    // Test freelancer can't bid on the same project
    
    // TODD: move to utils and use in all other test with defaults and option to override them
    private User getTestUser() {
        UserDto userDto = UserDto.builder().email( "freelancer@test.com" )
                .password( "password" )
                .passwordConfirm( "password" )
                .nickname( "tasty" )
                .firstName( "Test" )
                .lastName( "User" )
                .civilId( new byte[]{} )
                .phoneNumber( "+963987654321" )
                .address( "Street" )
                .type( "Freelancer" )
                .build();

        return userMapper.userDtoToUser( userDto );
    }
}
