package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** Redirect root URL to Swagger UI so users land on the API docs. */
    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui.html";
    }
}
