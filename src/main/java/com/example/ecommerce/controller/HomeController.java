// src/main/java/com/example/ecommerce/controller/HomeController.java
package com.example.ecommerce.controller;

import com.example.ecommerce.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final CatalogService catalogService;

    public HomeController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // --- CORRECCIÓN: La ruta principal de contenido ahora es "/home" ---
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("products", catalogService.findAll().stream().limit(8).toList());
        return "home";
    }
}
