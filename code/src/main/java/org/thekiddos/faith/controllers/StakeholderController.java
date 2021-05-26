package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.dtos.ProjectDto;
import org.thekiddos.faith.models.Stakeholder;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.ProjectService;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping( value = "stakeholder" )
public class StakeholderController {
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public StakeholderController( ProjectService projectService, UserService userService ) {
        this.projectService = projectService;
        this.userService = userService;
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

    @PostMapping( value = "/my-projects/add" )
    public String createNewProject( Model model, @Valid ProjectDto projectDto, BindingResult binding, Principal principal ) {
        if ( binding.hasErrors() ) {
            model.addAttribute( "project", projectDto );
            return "stakeholder/projects/new";
        }

        User user = (User) userService.loadUserByUsername( principal.getName() );
        projectService.createProjectFor( (Stakeholder)user.getType(), projectDto );
        return "redirect:/stakeholder/my-projects";
    }
}
