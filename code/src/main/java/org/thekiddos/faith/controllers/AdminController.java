package org.thekiddos.faith.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "admin")
public class AdminController {
    @GetMapping
    String getAdminPanel() {
        return "admin/home";
    }
}
