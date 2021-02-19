package org.thekiddos.faith.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.dtos.UserDto;
import org.thekiddos.faith.models.User;
import org.thekiddos.faith.services.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "register")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController( UserService userService ) {
        this.userService = userService;
    }

    @GetMapping
    public String get( Model model ) {
        model.addAttribute( "user", UserDto.builder().build() );
        return "accounts/register";
    }

    @PostMapping
    public String createUser( @Valid UserDto userDto, BindingResult binding, Model model ) {
        if ( binding.hasErrors() ) {
            model.addAttribute( "user", userDto );
            return "accounts/register";
        }
        User user = userService.createUser( userDto );
        userService.requireAdminApprovalFor( user );
        return "redirect:/register/success";
    }

    @GetMapping( value = "/success")
    public String getSuccess() {
        return "accounts/success";
    }
}
