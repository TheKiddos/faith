package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.mappers.FreelancerMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

import java.security.Principal;

@Controller
@RequestMapping( value = "freelancer" )
public class FreelancerController {
    private final UserService userService;
    private final FreelancerMapper freelancerMapper = FreelancerMapper.INSTANCE;

    @Autowired
    public FreelancerController( UserService userService ) {
        this.userService = userService;
    }

    @GetMapping( value = "/profile" )
    public String getProfilePage( Model model, Principal principal ) {
        var user = (User) userService.loadUserByUsername( principal.getName() );
        FreelancerDto freelancer = freelancerMapper.toDto( (Freelancer) user.getType() );
        model.addAttribute( "freelancer", freelancer );
        return "freelancer/profile";
    }
}
