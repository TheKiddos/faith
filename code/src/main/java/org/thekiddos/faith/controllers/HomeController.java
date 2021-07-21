package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.services.FreelancerService;
import org.thekiddos.faith.services.ProjectService;

@Controller
@RequestMapping( value = "/" )
public class HomeController {
    private final ProjectService projectService;
    private final FreelancerService freelancerService;

    @Autowired
    public HomeController( ProjectService projectService, FreelancerService freelancerService ) {
        this.projectService = projectService;
        this.freelancerService = freelancerService;
    }

    @GetMapping
    public String getHome( Model model ) {
        model.addAttribute( "projects", projectService.findFeaturedProjectsDto() );
        model.addAttribute( "freelancers", freelancerService.findFeaturedFreelancersDto() );
        return "home/home";
    }
}
