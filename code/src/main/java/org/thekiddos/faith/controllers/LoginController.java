package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thekiddos.faith.services.UserService;

@Controller
public class LoginController {
    private final UserService userService;

    public LoginController( UserService userService ) {
        this.userService = userService;
    }

    @GetMapping( value = "/login" )
    String get() {
        return "accounts/login";
    }

    @GetMapping( value = "/forgot-password" )
    String getForgotPasswordPage() {
        return "accounts/forgot-password";
    }

    @PostMapping( value = "/forgot-password" )
    String generateToken( String email ) {
        userService.createForgotPasswordToken( email );
        return "accounts/forgot-password";
    }
}
