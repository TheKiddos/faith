package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "register")
public class RegistrationController {
    @GetMapping
    public String get() {
        return "accounts/register";
    }
}