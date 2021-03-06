package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thekiddos.faith.services.UserService;

@Controller
@RequestMapping(value = "admin")
public class AdminController {
    private final UserService userService;

    public AdminController( UserService userService ) {
        this.userService = userService;
    }

    @GetMapping
    String getAdminPanel() {
        return "admin/home";
    }

    @GetMapping( value = "/users" )
    String getUsersAdminPanel( Model model ) {
        model.addAttribute( "users", userService.getAll() );
        return "admin/users";
    }

    @PostMapping( value = "/users/activate/{nickname}" )
    String activateUser( @PathVariable String nickname ) {
        userService.activateUser( nickname );
        return "redirect:/admin/users";
    }

    @PostMapping( value = "/users/delete/{nickname}" )
    String deleteUser( @PathVariable String nickname ) {
        userService.deleteUser( nickname );
        return "redirect:/admin/users";
    }
}
