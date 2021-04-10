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
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.EmailService;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;
import org.thekiddos.faith.utils.EmailSubjectConstants;
import org.thekiddos.faith.utils.EmailTemplatesConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class BidTest {
    private final BidService bidService;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    @MockBean
    private EmailService emailService;
    
    private Project project;
    private User freelancerUser;
    
    @Autowired
    public BidTest( BidService bidService,
                    BidRepository bidRepository, UserRepository userRepository,
                    ProjectRepository projectRepository,
                    UserService userService,
                    ProjectService projectService ) {
        this.bidService = bidService;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
    }
    
    @AfterEach
    public void tearDown() {
        bidRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        bidRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

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
        this.freelancerUser = userRepository.save( getTestUser() );
    }

    /**
     * Test adding a bid without a comment
     */
    @Test
    void addBid() {
        double amount = 10.0;
        
        BidDto dto = BidDto.builder()
                           .amount( amount )
                           .comment( "Pleeeeeeeeese" )
                           .projectid( this.project.getid() )
                           .build();
        assertTrue( bidRepository.findAll().isEmpty() );
        bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() );

        var user = (User)userService.loadUserByUsername( this.freelancerUser.getEmail() );
        var bids = bidRepository.findAll();
        assertEquals( 1, bids.size() );

        Bid bid = bids.stream().findFirst().orElse( null );
        assertNotNull( bid );
        assertNotNull( bid.getId() );
        assertEquals( "Bid of " + amount, bid.toString() );
        assertEquals( amount, bid.getAmount() );
        assertEquals( this.project, bid.getProject() );
        assertEquals( user.getType(), bid.getBidder() );
        
        var comments = bidCommentRepository.findAll();
        assertEquals( 1, comments.size() );
        
        Comment comment = commens.stream().findFirst().orElse( null );
        assertNotNull( comment );
        assertEquals( bid, comment.getBid() );
        assertEquals( "Pleeeeeeeeese", comment.getText() );

        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }

    @Test
    void equalsAndHashcode() {
        Bid bid = new Bid();
        bid.setAmount( 200 );

        Bid bid2 = new Bid();
        bid2.setAmount( 200 );

        Bid biggerBid = new Bid();
        biggerBid.setAmount( 300 );

        assertEquals( bid, bid );
        assertEquals( bid, bid2 );
        assertNotEquals( bid, biggerBid );
        assertNotEquals( bid, null );
        assertNotEquals( null, bid );

        assertEquals( bid.hashCode(), bid2.hashCode() );
        assertNotEquals( bid.hashCode(), biggerBid.hashCode() );
    }

    @Test
    void equalsAndHashCodeOnlyAffectedByAmount() {
        Bid bid = new Bid();
        bid.setAmount( 200 );
        bid.setProject( this.project );
        bid.setBidder( (Freelancer) this.freelancerUser.getType() );

        Bid bid2 = new Bid();
        bid2.setAmount( 200 );

        assertEquals( bid, bid2 );
        assertEquals( bid.hashCode(), bid2.hashCode() );
    }
    
    // TODO: Create mapper for bid
    // Test bid with an empty comment won't create a comment
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
