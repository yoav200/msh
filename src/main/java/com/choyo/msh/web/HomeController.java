package com.choyo.msh.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping({"/"})
    public String home(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }

    @GetMapping({"/terms"})
    public String terms(Model model) {
        model.addAttribute("appName", appName);
        return "terms";
    }

    @GetMapping({"/privacy"})
    public String privacy(Model model) {
        model.addAttribute("appName", appName);
        return "privacy";
    }

}