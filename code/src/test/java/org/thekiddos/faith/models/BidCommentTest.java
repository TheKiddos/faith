package org.thekiddos.faith.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.mappers.BidCommentMapper;
import org.thekiddos.faith.mappers.UserMapper;
import org.thekiddos.faith.repositories.BidCommentRepository;
import org.thekiddos.faith.repositories.BidRepository;
import org.thekiddos.faith.repositories.ProjectRepository;
import org.thekiddos.faith.repositories.UserRepository;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ExtendWith( SpringExtension.class )
public class BidCommentTest {
    private final BidService bidService;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final BidCommentService bidCommentService;
    private final BidCommentRepository bidCommentRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final BidCommentMapper bidCommentMapper;

    private Project project;
    private User freelancerUser;
    private Bid bid;
    private BidComment bidComment;

    @Autowired
    public BidCommentTest( BidService bidService,
                           BidRepository bidRepository, UserRepository userRepository,
                           ProjectRepository projectRepository,
                           UserService userService,
                           ProjectService projectService, BidCommentService bidCommentService, BidCommentRepository bidCommentRepository, BidCommentMapper bidCommentMapper ) {
        this.bidService = bidService;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectService = projectService;
        this.bidCommentService = bidCommentService;
        this.bidCommentRepository = bidCommentRepository;
        this.bidCommentMapper = bidCommentMapper;
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

        BidDto dto = BidDto.builder()
                .amount( 20.0 )
                .comment( "Pleeeeeeeeese" )
                .projectId( this.project.getId() )
                .build();
        bidService.addBid( dto, (Freelancer)this.freelancerUser.getType() );
        this.bid = bidRepository.findAll().stream().findFirst().orElse( null );
        this.bidComment = bidCommentRepository.findAll().stream().findFirst().orElse( null );
    }

    @Test
    void addComment() {
        var dto = BidCommentDto.builder()
                .text( "New Comment" )
                .bidId( this.bid.getId() )
                .build();

        assertEquals( 1, bidCommentRepository.findAll().size() );
        bidCommentService.addComment( dto );

        var comments = bidCommentRepository.findAll();
        assertEquals( 2, comments.size() );
        comments.remove( this.bidComment );

        var newComment = comments.get( 0 );
        assertEquals( "New Comment", newComment.getText() );
        assertEquals( this.bid, newComment.getBid() );
    }

    @Test
    void findByBid() {
        BidDto dto = BidDto.builder()
                .amount( 20.0 )
                .comment( "Dieeeeeeee" )
                .projectId( project.getId() )
                .build();
        var user = getTestUser();
        user.setEmail( "newuser@user.com" );
        user.setNickname( "newuser" );
        user = userRepository.save( user );
        bidService.addBid( dto, (Freelancer)user.getType() );

        var commentDto = BidCommentDto.builder()
                .text( "New Comment" )
                .bidId( this.bid.getId() )
                .build();
        bidCommentService.addComment( commentDto );

        var allComments = bidCommentRepository.findAll();
        assertEquals( 3, allComments.size() );

        assertEquals( allComments.stream().filter( comment -> comment.getBid().getId().equals( this.bid.getId() ) ).collect( Collectors.toList() ), bidCommentService.findByBid( this.bid ) );
    }

    @Test
    void string() {
        BidComment comment = new BidComment();
        comment.setText( "Die" );
        assertEquals( "BidComment(text='Die')", comment.toString() );

        BidComment emptyComment = new BidComment();
        assertEquals( "BidComment(text='null')", emptyComment.toString() );
    }

    @Test
    void equalsAndHashcode() {
        BidComment comment = new BidComment();
        comment.setText( "Die" );

        BidComment sameComment = new BidComment();
        sameComment.setText( "Die" );

        BidComment anotherComment = new BidComment();
        anotherComment.setText( "Live" );

        assertEquals( comment, comment );
        assertEquals( comment, sameComment );
        assertNotEquals( comment, anotherComment );
        assertNotEquals( comment, null );
        assertNotEquals( null, comment );

        assertEquals( comment.hashCode(), sameComment.hashCode() );
        assertNotEquals( comment.hashCode(), anotherComment.hashCode() );
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
}
