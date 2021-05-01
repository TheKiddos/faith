package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;

@Controller
@RequestMapping( value = "bids" )
public class BidCommentController {
    private final BidService bidService;
    private final BidCommentService bidCommentService;

    public BidCommentController( BidService bidService, BidCommentService bidCommentService ) {
        this.bidService = bidService;
        this.bidCommentService = bidCommentService;
    }

    @GetMapping( value = "{id}/comments" )
    public String getBidComments( @PathVariable Long id, Model model ) {
        Bid bid = bidService.findById( id );
        model.addAttribute( "comments", bidCommentService.findByBidDto( bid ) );
        return "bids/list-ajax";
    }
}
