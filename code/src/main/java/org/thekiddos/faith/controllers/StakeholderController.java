package org.thekiddos.faith.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.exceptions.ProjectNotFoundException;
import org.thekiddos.faith.exceptions.ProposalNotFoundException;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.*;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping( value = "stakeholder" )
public class StakeholderController {
    private final ProjectService projectService;
    private final UserService userService;
    private final FreelancerService freelancerService;
    private final ProposalService proposalService;
    private final FreelancerRatingService freelancerRatingService;

    @Autowired
    public StakeholderController( ProjectService projectService, UserService userService, FreelancerService freelancerService, ProposalService proposalService, FreelancerRatingService freelancerRatingService ) {
        this.projectService = projectService;
        this.userService = userService;
        this.freelancerService = freelancerService;
        this.proposalService = proposalService;
        this.freelancerRatingService = freelancerRatingService;
    }

    @GetMapping( value = "/my-projects" )
    public String getMyProjectsPage( Model model, Principal principal ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );
        model.addAttribute( "projects", projectService.findByOwnerDto( (Stakeholder) user.getType() ) );
        return "stakeholder/projects/my-projects";
    }

    @GetMapping( value = "/my-projects/add" )
    public String getCreateNewProjectPage( Model model ) {
        model.addAttribute( "project", ProjectDto.builder().build() );
        return "stakeholder/projects/new";
    }

    @PostMapping(value = "/my-projects/add")
    public String createNewProject( Model model, @Valid @ModelAttribute("project") ProjectDto projectDto, BindingResult binding, Principal principal ) {
        if ( binding.hasErrors() ) {
            model.addAttribute( "project", projectDto );
            return "stakeholder/projects/new";
        }

        User user = (User) userService.loadUserByUsername( principal.getName() );
        projectService.createProjectFor( (Stakeholder) user.getType(), projectDto );
        return "redirect:/stakeholder/my-projects";
    }

    @GetMapping( value = "/my-projects/{id}" )
    public String getProjectDashboard( Model model, Principal principal, @PathVariable Long id ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );
        ProjectDto projectDto;
        Project project;
        try {
            projectDto = projectService.findByIdForOwnerDto( (Stakeholder) user.getType(), id );
        }
        catch ( ProjectNotFoundException e ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getMessage(), e );
        }
        model.addAttribute( "project", projectDto );

        project = projectService.findById( id );
        try {
            var proposal = proposalService.findFreelancerAcceptedProposalFor( project );
            model.addAttribute( "proposal", proposal );
        }
        catch ( ProposalNotFoundException e ) {
            model.addAttribute( "proposal", null );
            model.addAttribute( "freelancers", freelancerService.getAvailableFreelancersDto( project ) );
        }

        return "stakeholder/projects/dashboard";
    }

    @PostMapping( value = "/my-projects/close/{id}" )
    public String closeProject( Principal principal, @PathVariable Long id ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );
        Project project;
        try {
            project = projectService.findById( id );
            // TODO: move logic to service maybe create findByOwnerAndId method
            if ( !project.getOwner().getUser().equals( user ) )
                throw new ProjectNotFoundException();
        } catch ( ProjectNotFoundException e ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getMessage(), e );
        }

        projectService.closeProject( project );

        return "redirect:/stakeholder/my-projects/" + id;
    }

    @PostMapping( value = "/my-projects/rate/{id}" )
    public String rateFreelancer( Principal principal, @PathVariable Long id, Integer value ) {
        User user = (User) userService.loadUserByUsername( principal.getName() );

        try {
            var project = projectService.findById( id );
            // TODO: move logic to service maybe create findByOwnerAndId method
            if ( !project.getOwner().getUser().equals( user ) || !project.isClosed() )
                throw new ProjectNotFoundException();
            var proposal = proposalService.findFreelancerAcceptedProposalFor( project );

            freelancerRatingService.rate( proposal.getFreelancer(), value );
        } catch ( ProjectNotFoundException | ProposalNotFoundException e ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getMessage(), e );
        }

        return "redirect:/stakeholder/my-projects/" + id;
    }
}
