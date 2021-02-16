package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
