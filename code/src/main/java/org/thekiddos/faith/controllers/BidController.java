package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping( value = "freelancer/bid" )
public class BidController {
    private final BidService bidService;
    private final UserService userService;

    @Autowired
    public BidController( BidService bidService, UserService userService ) {
        this.bidService = bidService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> addBid( @Valid BidDto bidDto, BindingResult binding, Principal principal ) {
        if ( binding.hasErrors() ) {
            // TODO: do we return the error details? This is intended for browser only and everything should be valid in the first place
            return new ResponseEntity<>( "Something went wrong please try again", HttpStatus.BAD_REQUEST );
        }

        User user = (User) userService.loadUserByUsername( principal.getName() );
        bidService.addBid( bidDto, (Freelancer)user.getType() );
        return new ResponseEntity<>( "Your bid was successfully added", HttpStatus.CREATED );
    }

    @ExceptionHandler( RuntimeException.class )
    public ResponseEntity<String> handleError( RuntimeException e ) {

        return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
    }
}
