package org.thekiddos.faith.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void addBid() throws Exception {
        Bid bid = new Bid();
        bid.setId( 1L );

        Mockito.doReturn( bid ).when( bidService ).findById( bid.getId() );
        Mockito.doReturn( List.of() ).when( bidCommentService ).findByBidDto( bid );

        mockMvc.perform( get( "/bids/" + bid.getId() + "/comments" ) ).andExpect( status().isOk() );

        Mockito.verify( bidService, Mockito.times( 1 ) ).findById( bid.getId() );
        Mockito.verify( bidCommentService, Mockito.times( 1 ) ).findByBidDto( bid );
    }
}
