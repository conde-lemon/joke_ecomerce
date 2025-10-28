package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    /**
     * Redirige la URL raíz ("/") a la página principal de contenido ("/home").
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}
    