package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.dtos.BidDto;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import java.security.Principal;

@Controller
@RequestMapping( value = "projects" )
public class PublicProjectController {
    private final ProjectService projectService;
    private final BidService bidService;
    private final UserService userService;

    @Autowired
    public PublicProjectController( ProjectService projectService, BidService bidService, UserService userService ) {
        this.projectService = projectService;
        this.bidService = bidService;
        this.userService = userService;
    }

    @GetMapping
    public String projectList( Model model ) {
        model.addAttribute( "projects", projectService.findAllDto() );
        return "projects/list";
    }

    @GetMapping( value = "/{id}")
    public String projectDetails( Model model, @PathVariable Long id, Principal principal ) {
        // TODO: there is too much inconsistency between what's passed to a context (Dto vs Entity) do we need to worry about this?
        Project project = projectService.findById( id );

        boolean canBid = false;
        if ( !( principal == null ) ) {
            User user = (User) userService.loadUserByUsername( principal.getName() );
            canBid = bidService.canBidOnProject( user, project );
        }

        model.addAttribute( "project", project );
        model.addAttribute( "bids", bidService.findByProjectDto( project ) );
        model.addAttribute( "canBid", canBid );
        model.addAttribute( "newBid", BidDto.builder().projectId( project.getId() ).build() );

        return "projects/details";
    }
}
