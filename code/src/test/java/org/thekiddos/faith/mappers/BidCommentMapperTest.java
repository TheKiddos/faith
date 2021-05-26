package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.UserService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith( MockitoExtension.class )
public class BidCommentMapperTest {
    @InjectMocks
    private BidCommentMapperImpl bidCommentMapper;  // Using the implementation so we can inject Mocks
    @Mock
    private BidService bidService;
    @Mock
    private UserService userService;
    private Bid bid;

    @BeforeEach
    public void setUp() {
        Bid bid = new Bid();
        bid.setBidder( new Freelancer() );
        bid.setProject( new Project() );
        bid.setAmount( 20.0 );
        bid.setId( 1L );
        this.bid = bid;
    }

    @Test
    void toEntity() {
        BidCommentDto dto = BidCommentDto.builder()
                .text( "Pleeeeeeeeese" )
                .bidId( this.bid.getId() )
                .user( UserDto.builder().email( "test@gmail.com" ).build() )
                .build();

        User user = new User();
        user.setEmail( "test@gmail.com" );

        Mockito.doReturn( this.bid ).when( bidService ).findById( this.bid.getId() );
        Mockito.doReturn( user ).when( userService ).findByEmail( user.getEmail() );

        BidComment comment = bidCommentMapper.toEntity( dto );
        assertEquals( dto.getText(), comment.getText() );
        assertEquals( dto.getBidId(), comment.getBid().getId() );
        assertEquals( dto.getUser().getEmail(), comment.getUser().getEmail() );
    }

    @Test
    void toEntityInvalidBid() {
        BidCommentDto dto = BidCommentDto.builder()
                .text( "Pleeeeeeeeese" )
                .bidId( this.bid.getId() )
                .user( UserDto.builder().email( "test@gmail.com" ).build() )
                .build();

        Mockito.doThrow( BidNotFoundException.class ).when( bidService ).findById( this.bid.getId() );
        // As it turns out the runtime exception is due to MapStruct way of implementing
        assertThrows( RuntimeException.class, () -> bidCommentMapper.toEntity( dto ) );
    }

    @Test
    void toEntityInvalidUser() {
        BidCommentDto dto = BidCommentDto.builder()
                .text( "Pleeeeeeeeese" )
                .bidId( this.bid.getId() )
                .user( UserDto.builder().email( "test@gmail.com" ).build() )
                .build();

        User user = new User();
        user.setEmail( "test@gmail.com" );

        Mockito.doReturn( this.bid ).when( bidService ).findById( this.bid.getId() );
        Mockito.doThrow( UsernameNotFoundException.class ).when( userService ).findByEmail( user.getEmail() );

        assertThrows( UsernameNotFoundException.class, () -> bidCommentMapper.toEntity( dto ) );
    }

    @Test
    void toDto() {
        User user = new User();
        user.setEmail( "test@gmail.com" );

        BidComment comment = new BidComment();
        comment.setText( "hh" );
        comment.setBid( this.bid );
        comment.setUser( user );

        BidCommentDto dto = bidCommentMapper.toDto( comment );
        assertEquals( comment.getText(), dto.getText() );
        assertEquals( comment.getBid().getId(), dto.getBidId() );
        assertEquals( comment.getUser().getEmail(), dto.getUser().getEmail() );
    }

    @Test
    void nullTest() {
        assertNull( bidCommentMapper.toEntity( null ) );
    }
}
