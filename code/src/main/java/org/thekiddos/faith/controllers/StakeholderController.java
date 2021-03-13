package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = "stakeholder" )
public class StakeholderController {
    @GetMapping( value = "/my-projects" )
    public String getMyProjectsPage() {
        return "stakeholder/projects/my-projects";
    }

    @GetMapping( value = "/my-projects/add" )
    public String getCreateNewProjectPage() {
        return "stakeholder/projects/new";
    }
}
