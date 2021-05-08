package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.services.ProjectService;

@Controller
@RequestMapping( value = "/" )
public class HomeController {
    private final ProjectService projectService;

    @Autowired
    public HomeController( ProjectService projectService ) {
        this.projectService = projectService;
    }

    @GetMapping
    public String projectList( Model model ) {
        model.addAttribute( "projects", projectService.findFeaturedProjectsDto() );
        return "home/home";
    }
}
