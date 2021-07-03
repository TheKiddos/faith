package org.thekiddos.faith.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.exceptions.InvalidTransitionException;
import org.thekiddos.faith.exceptions.ProposalNotAllowedException;
import org.thekiddos.faith.models.*;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.ProposalService;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class ProposalController {
    private final UserService userService;
    private final ProjectService projectService;
    private final ProposalService proposalService;

    @Autowired
    public ProposalController( ProjectService projectService, ProposalService proposalService, UserService userService ) {
        this.projectService = projectService;
        this.proposalService = proposalService;
        this.userService = userService;
    }

    @PostMapping( value = "stakeholder/propose" ) @ResponseBody
    public ResponseEntity<String> sendProposal( @Valid ProposalDto proposalDto, BindingResult binding, Principal principal ) {
        if ( binding.hasErrors() ) {
            return new ResponseEntity<>( "Please check form errors", HttpStatus.BAD_REQUEST );
        }

        User user = (User) userService.loadUserByUsername( principal.getName() );
        
        // Make sure that the stakeholder can only send proposals for his projects
        // TODO: refactor or create another method
        projectService.findByIdForOwnerDto( (Stakeholder) user.getType(), proposalDto.getProjectId() );
        
        proposalService.sendProposal( proposalDto );
        return new ResponseEntity<>( "Your proposal was sent", HttpStatus.CREATED );
    }
    
    @ExceptionHandler( RuntimeException.class )
    public ResponseEntity<String> handleError( RuntimeException e ) {

        return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @GetMapping( value = "/freelancer/proposals" )
    public String getFreelancerProposals( Model model, Principal principal ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );
        model.addAttribute( "proposals", proposalService.findNewFreelancerProposals( (Freelancer) user.getType() ) );
        return "freelancer/proposals";
    }

    @PostMapping( value = "/freelancer/proposals/reject/{id}" )
    public String rejectProposal( Principal principal, @PathVariable Long id ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );
        try {
            Proposal proposal = proposalService.findProposalFor( (Freelancer) user.getType(), id );
            proposalService.setStatus( proposal, Status.REJECTED );
        }
        catch ( ProposalNotAllowedException | InvalidTransitionException e ) {
            log.error( e.getMessage() );
        }
        return "redirect:/freelancer/proposals";
    }
    
    @GetMapping( value = "/freelancer/proposals/count" ) @ResponseBody
    public ResponseEntity<Map<String, Integer>> getFreelancerProposals( Principal principal ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );
        // TODO: remove logic from here
        Map<String, Integer> result = new HashMap<>();
        result.put( "proposalsCount", proposalService.findNewFreelancerProposals( (Freelancer) user.getType() ).size() );
        return new ResponseEntity<>( result, HttpStatus.OK );
    }
}
