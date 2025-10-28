package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // === NUEVO: listado de catálogo con búsqueda y filtros ===
    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "min", required = false) BigDecimal min,
            @RequestParam(name = "max", required = false) BigDecimal max,
            Model model
    ) {
        List<Product> all = catalogService.findAll();
        List<Product> filtered = catalogService.search(q, min, max);

        model.addAttribute("total", all.size());
        model.addAttribute("count", filtered.size());
        model.addAttribute("products", filtered);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("min", min);
        model.addAttribute("max", max);

        return "catalog";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable("id") Long id, Model model) {
        Product p = catalogService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("product", p);
        return "product-detail";
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public Product productPreview(@PathVariable("id") Long id) {
        return catalogService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
