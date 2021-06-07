package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thekiddos.faith.dtos.ProposalDto;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.ProposalService;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping( value = "stakeholder" )
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

    @PostMapping( value = "/propose" )
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
}
