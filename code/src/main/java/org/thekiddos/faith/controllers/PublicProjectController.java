package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.models.Project;
import org.thekiddos.faith.services.BidService;
import org.thekiddos.faith.services.ProjectService;

@Controller
@RequestMapping( value = "projects" )
public class PublicProjectController {
    private final ProjectService projectService;
    private final BidService bidService;

    @Autowired
    public PublicProjectController( ProjectService projectService, BidService bidService ) {
        this.projectService = projectService;
        this.bidService = bidService;
    }

    @GetMapping
    public String projectList( Model model ) {
        model.addAttribute( "projects", projectService.findAllDto() );
        return "projects/list";
    }

    @GetMapping( value = "/{id}")
    public String projectDetails( Model model, @PathVariable Long id ) {
        // TODO: there is too much inconsistency between what's passed to a context (Dto vs Entity) do we need to worry about this?
        Project project = projectService.findById( id );
        model.addAttribute( "project", project );
        model.addAttribute( "bids", bidService.findByProjectDto( project ) );
        // TODO: Can I access current user in thymeleaf without passing it
        return "projects/details";
    }
}
