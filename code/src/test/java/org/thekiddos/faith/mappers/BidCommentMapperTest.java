package org.thekiddos.faith.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.models.BidComment;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.services.BidService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith( MockitoExtension.class )
public class BidCommentMapperTest {
    @InjectMocks
    private BidCommentMapperImpl bidCommentMapper;  // Using the implementation so we can inject Mocks
    @Mock
    private BidService bidService;
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
                .build();

        Mockito.doReturn( this.bid ).when( bidService ).findById( this.bid.getId() );
        BidComment comment = bidCommentMapper.toEntity( dto );
        assertEquals( dto.getText(), comment.getText() );
        assertEquals( dto.getBidId(), comment.getBid().getId() );
    }

    @Test
    void toEntityInvalidBid() {
        BidCommentDto dto = BidCommentDto.builder()
                .text( "Pleeeeeeeeese" )
                .bidId( this.bid.getId() )
                .build();

        Mockito.doThrow( BidNotFoundException.class ).when( bidService ).findById( this.bid.getId() );
        // As it turns out the runtime exception is due to MapStruct way of implementing
        assertThrows( RuntimeException.class, () -> bidCommentMapper.toEntity( dto ) );
    }

    @Test
    void toDto() {
        BidComment comment = new BidComment();
        comment.setText( "hh" );
        comment.setBid( this.bid );

        BidCommentDto dto = bidCommentMapper.toDto( comment );
        assertEquals( comment.getText(), dto.getText() );
        assertEquals( comment.getBid().getId(), dto.getBidId() );
    }

    @Test
    void nullTest() {
        assertNull( bidCommentMapper.toEntity( null ) );
    }
}
