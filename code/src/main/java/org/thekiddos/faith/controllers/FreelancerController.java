package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.dtos.FreelancerDto;
import org.thekiddos.faith.mappers.FreelancerMapper;
import org.thekiddos.faith.models.Freelancer;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.FreelancerService;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping( value = "freelancer" )
public class FreelancerController {
    private final UserService userService;
    private final FreelancerMapper freelancerMapper = FreelancerMapper.INSTANCE;
    private final FreelancerService freelancerService;

    @Autowired
    public FreelancerController( UserService userService, FreelancerService freelancerService ) {
        this.userService = userService;
        this.freelancerService = freelancerService;
    }

    @GetMapping( value = "/profile" )
    public String getProfilePage( Model model, Principal principal ) {
        var user = (User) userService.loadUserByUsername( principal.getName() );
        FreelancerDto freelancer = freelancerMapper.toDto( (Freelancer) user.getType() );
        model.addAttribute( "freelancer", freelancer );
        return "freelancer/profile";
    }

    @PostMapping( value = "/profile" )
    public String updateProfile( Model model, Principal principal, @Valid FreelancerDto freelancerDto, BindingResult binding ) {
        if ( binding.hasErrors() ) {
            model.addAttribute( "freelancer", freelancerDto );
            return "freelancer/profile";
        }

        var user = (User) userService.loadUserByUsername( principal.getName() );
        freelancerService.updateProfile( user, freelancerDto );
        return "redirect:/freelancer/profile";
    }
}
