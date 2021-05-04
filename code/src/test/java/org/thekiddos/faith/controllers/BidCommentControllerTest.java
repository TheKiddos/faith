package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BidCommentControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private BidService bidService;
    @MockBean
    private BidCommentService bidCommentService;

    @Autowired
    BidCommentControllerTest( MockMvc mockMvc ) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getBidCommentsOk() throws Exception {
        Bid bid = new Bid();
        bid.setId( 1L );

        Mockito.doReturn( bid ).when( bidService ).findById( bid.getId() );
        Mockito.doReturn( List.of() ).when( bidCommentService ).findByBidDto( bid );

        mockMvc.perform( get( "/bids/" + bid.getId() + "/comments" ) ).andExpect( status().isOk() );

        Mockito.verify( bidService, Mockito.times( 1 ) ).findById( bid.getId() );
        Mockito.verify( bidCommentService, Mockito.times( 1 ) ).findByBidDto( bid );
    }

    @Test
    void addBidCommentNoCommenter() throws Exception {
        Bid bid = new Bid();
        bid.setId( 1L );

        BidCommentDto dto = BidCommentDto.builder()
                .bidId( bid.getId() )
                .text( "Hello" )
                .build();

        Mockito.doReturn( bid ).when( bidService ).findById( bid.getId() );

        mockMvc.perform( post( "/bids/comments/add" )
                .with( csrf() )
                .param( "text", dto.getText() )
                .param( "bidId", String.valueOf( dto.getBidId() ) ) )
                .andExpect( status().isUnauthorized() );

        Mockito.verify( bidService, Mockito.times( 0 ) ).findById( any() );
        Mockito.verify( bidCommentService, Mockito.times( 0 ) ).addComment( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void addBidCommentInvalid() throws Exception {
        Bid bid = new Bid();
        bid.setId( 1L );

        BidCommentDto dto = BidCommentDto.builder()
                .bidId( bid.getId() )
                .text( "" )
                .build();

        Mockito.doReturn( bid ).when( bidService ).findById( bid.getId() );

        mockMvc.perform( post( "/bids/comments/add" )
                .with( csrf() )
                .param( "text", dto.getText() )
                .param( "bidId", String.valueOf( dto.getBidId() ) ) )
                .andExpect( status().isBadRequest() );

        Mockito.verify( bidService, Mockito.times( 1 ) ).findById( bid.getId() );
        Mockito.verify( bidCommentService, Mockito.times( 0 ) ).addComment( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void addBidCommentNoBid() throws Exception {
        BidCommentDto dto = BidCommentDto.builder()
                .bidId( -1L )
                .text( "" )
                .build();

        Mockito.doThrow( BidNotFoundException.class ).when( bidService ).findById( -1L );

        mockMvc.perform( post( "/bids/comments/add" )
                .with( csrf() )
                .param( "text", dto.getText() )
                .param( "bidId", String.valueOf( dto.getBidId() ) ) )
                .andExpect( status().isNotFound() );

        Mockito.verify( bidService, Mockito.times( 1 ) ).findById( -1L );
        Mockito.verify( bidCommentService, Mockito.times( 0 ) ).addComment( any() );
    }

    @Test
    @WithMockUser( authorities = {"USER", "FREELANCER"}, username = "freelancer@test.com" )
    void addBidComment() throws Exception {
        Bid bid = new Bid();
        bid.setId( 1L );

        BidCommentDto dto = BidCommentDto.builder()
                .bidId( bid.getId() )
                .text( "Hello" )
                .byEmail( "freelancer@test.com" )
                .build();

        Mockito.doReturn( bid ).when( bidService ).findById( bid.getId() );

        mockMvc.perform( post( "/bids/comments/add" )
                .with( csrf() )
                .param( "text", dto.getText() )
                .param( "bidId", String.valueOf( dto.getBidId() ) ) )
                .andExpect( status().isCreated() );

        Mockito.verify( bidService, Mockito.times( 1 ) ).findById( bid.getId() );
        Mockito.verify( bidCommentService, Mockito.times( 1 ) ).addComment( dto );
    }
}
