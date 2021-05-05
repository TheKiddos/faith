package org.thekiddos.faith.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.dtos.BidCommentDto;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.exceptions.BidNotFoundException;
import org.thekiddos.faith.models.Bid;
import org.thekiddos.faith.services.BidCommentService;
import org.thekiddos.faith.services.BidService;

import javax.validation.Valid;
import java.security.Principal;

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
        return "bids/partials/list-ajax";
    }

    @PostMapping( value = "comments/add" )
    public ResponseEntity<String> addBidComment( Principal principal, @Valid BidCommentDto comment, BindingResult binding ) {
        if ( principal == null ) {
            return new ResponseEntity<>( "Please Log In", HttpStatus.UNAUTHORIZED );
        }

        var id = comment.getBidId();
        try {
            bidService.findById( id );
        }
        catch ( BidNotFoundException e ) {
            return new ResponseEntity<>( "No Such Bid", HttpStatus.NOT_FOUND );
        }

        comment.setUser( UserDto.builder().email( principal.getName() ).build() );
        comment.setBidId( id );

        if ( binding.hasErrors() ) {
            return new ResponseEntity<>( "Please Enter a comment", HttpStatus.BAD_REQUEST );
        }

        bidCommentService.addComment( comment );
        return new ResponseEntity<>( "Comment Added!", HttpStatus.CREATED );
    }
}
