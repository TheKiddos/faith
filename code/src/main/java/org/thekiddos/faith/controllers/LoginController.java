package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "login")
public class LoginController {
    @GetMapping
    String get() {
        return "accounts/login";
    }
}
