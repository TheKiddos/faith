package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.services.ProjectService;

@Controller
@RequestMapping( value = "projects" )
public class StakeholderController {
    private final ProjectService projectService;

    @Autowired
    public StakeholderController( ProjectService projectService ) {
        this.projectService = projectService;
    }

    @GetMapping
    public String proejctList( Model model ) {
        model.addAttribute( "projects", projectService.findAll() );
        return "projects/list";
    }
}
