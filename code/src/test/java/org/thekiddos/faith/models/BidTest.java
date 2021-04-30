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
import org.springframework.transaction.TransactionSystemException;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.BiddingNotAllowedException;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.BidCommentRepository;
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
    private final BidCommentRepository bidCommentRepository;
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
                    ProjectService projectService, BidCommentRepository bidCommentRepository ) {
        this.bidService = bidService;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
        this.bidCommentRepository = bidCommentRepository;
    }
    
    @AfterEach
    public void tearDown() {
        bidCommentRepository.deleteAll();
        bidRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        bidCommentRepository.deleteAll();
        bidRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        ProjectDto projectDto = getTestProjectDto();

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
     * Test adding a bid with a comment
     */
    @Test
    void addBid() {
        double amount = 10.0;
        
        BidDto dto = BidDto.builder()
                           .amount( amount )
                           .comment( "Pleeeeeeeeese" )
                           .projectId( this.project.getId() )
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
        
        BidComment comment = comments.stream().findFirst().orElse( null );
        assertNotNull( comment );
        assertEquals( bid, comment.getBid() );
        assertEquals( "Pleeeeeeeeese", comment.getText() );

        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }

    /**
     * Test adding a bid without a comment
     */
    @Test
    void addBidWithoutComment() {
        double amount = 10.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .projectId( this.project.getId() )
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
        assertTrue( comments.isEmpty() );

        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }

    @Test
    void addBidOnPrivateProjectIsInvalid() {
        this.project.setAllowBidding( false );
        this.project = this.projectRepository.save( this.project );

        double amount = 10.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "bhbh" )
                .projectId( this.project.getId() )
                .build();
        assertTrue( bidRepository.findAll().isEmpty() );
        assertThrows( BiddingNotAllowedException.class, () -> bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() ) );

        var bids = bidRepository.findAll();
        assertTrue( bids.isEmpty() );

        var comments = bidCommentRepository.findAll();
        assertTrue( comments.isEmpty() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( any(), any(), any(), any(), any() );
    }

    @Test
    void addAnotherBidIsInvalid() {
        double amount = 10.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( this.project.getId() )
                .build();
        assertTrue( bidRepository.findAll().isEmpty() );
        bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() );

        // adding another bid
        assertThrows( BiddingNotAllowedException.class, () -> bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() ) );

        var bids = bidRepository.findAll();
        assertEquals( 1, bids.size() );

        var comments = bidCommentRepository.findAll();
        assertEquals( 1, comments.size() );

        Mockito.verify( emailService, Mockito.times( 1 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }

    @Test
    void addBidWithNoProjectIsInvalid() {
        double amount = 10.0;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( -1 )
                .build();
        assertTrue( bidRepository.findAll().isEmpty() );

        // adding another bid
        // For Some reason maven is treating ProjectNotFoundException as RunTimeException
        assertThrows( RuntimeException.class, () -> bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() ) );

        var bids = bidRepository.findAll();
        assertEquals( 0, bids.size() );

        var comments = bidCommentRepository.findAll();
        assertEquals( 0, comments.size() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }

    @Test
    void addBidWithInvalidAmount() {
        double amount = 5;

        BidDto dto = BidDto.builder()
                .amount( amount )
                .comment( "Pleeeeeeeeese" )
                .projectId( this.project.getId() )
                .build();
        assertTrue( bidRepository.findAll().isEmpty() );

        // adding another bid
        assertThrows( TransactionSystemException.class, () -> bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() ) );

        var bids = bidRepository.findAll();
        assertEquals( 0, bids.size() );

        var comments = bidCommentRepository.findAll();
        assertEquals( 0, comments.size() );

        Mockito.verify( emailService, Mockito.times( 0 ) )
                .sendTemplateMail( eq( List.of( "bhbh@gmail.com" ) ), anyString(), eq( EmailSubjectConstants.NEW_BID ), eq( EmailTemplatesConstants.NEW_BID_TEMPLATE ), any() );
    }

    @Test
    void findByProject() {
        BidDto dto = BidDto.builder()
                .amount( 20.0 )
                .comment( "Pleeeeeeeeese" )
                .projectId( project.getId() )
                .build();
        bidService.addBid( dto, (Freelancer)freelancerUser.getType() );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        bidService.addBid( dto, (Freelancer)freelancer2.getType() );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        bidService.addBid( dto, (Freelancer)freelancerUser.getType() );

        var bids = bidService.findByProject( this.project );
        assertEquals( 2, bids.size() );
        assertTrue( bids.stream().allMatch( bid -> bid.getProject().equals( this.project ) ) );
    }

    @Test
    void findByProjectDto() {
        BidDto dto = BidDto.builder()
                .amount( 20.0 )
                .comment( "Pleeeeeeeeese" )
                .projectId( project.getId() )
                .build();
        bidService.addBid( dto, (Freelancer)freelancerUser.getType() );

        var freelancer2 = getTestUser();
        freelancer2.setEmail( "freelancer2@test.com" );
        freelancer2.setNickname( "freelancer2" );
        freelancer2 = userRepository.save( freelancer2 );
        bidService.addBid( dto, (Freelancer)freelancer2.getType() );

        var project2 = projectService.createProjectFor( this.project.getOwner(), getTestProjectDto() );
        dto.setProjectId( project2.getId() );
        bidService.addBid( dto, (Freelancer)freelancerUser.getType() );

        var bids = bidService.findByProjectDto( this.project );
        assertEquals( 2, bids.size() );
        assertTrue( bids.stream().allMatch( bid -> this.project.getId().equals( bid.getProjectId() ) ) );
        assertTrue( bids.stream().allMatch( bid -> bid.getBidComments().size() == 1 ) );
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

    @Test
    void canBidOnProjectTrue() {
        assertTrue( bidService.canBidOnProject( freelancerUser, project ) );
    }

    @Test
    void canBidOnProjectFalse() {
        BidDto dto = BidDto.builder()
                .amount( 20.0 )
                .comment( "Pleeeeeeeeese" )
                .projectId( this.project.getId() )
                .build();
        assertTrue( bidRepository.findAll().isEmpty() );
        bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() );

        assertFalse( bidService.canBidOnProject( freelancerUser, project ) );
    }

    @Test
    void canBidOnProjectNotFreelancer() {
        assertFalse( bidService.canBidOnProject( project.getOwner().getUser(), project ) );
    }

    // TODO: move to utils and use in all other test with defaults and option to override them
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

    private ProjectDto getTestProjectDto() {
        return ProjectDto.builder()
                .name( "new world order" )
                .description( "Make all people slaves" )
                .preferredBid( 200.0 )
                .duration( 31 )
                .minimumQualification( 100 )
                .allowBidding( true )
                .build();
    }
}
