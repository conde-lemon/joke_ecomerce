package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartRestController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart() {
        var cart = cartService.getCart();

        Map<String, Object> response = new HashMap<>();
        response.put("items", cart.getItems());
        response.put("totalItems", cart.getTotalItems());
        response.put("subtotal", cart.getSubtotal());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<Map<String, String>> addToCart(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int qty
    ) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            cartService.add(product, qty);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Producto agregado al carrito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<Map<String, String>> updateQuantity(
            @PathVariable Long productId,
            @RequestParam int qty
    ) {
        try {
            // Primero removemos el item
            cartService.remove(productId);

            // Luego lo agregamos con la nueva cantidad
            if (qty > 0) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                cartService.add(product, qty);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Cantidad actualizada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/remove/{productId}")
    public ResponseEntity<Map<String, String>> removeItem(@PathVariable Long productId) {
        try {
            cartService.remove(productId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Producto eliminado del carrito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCart() {
        cartService.clear();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Carrito vaciado");
        return ResponseEntity.ok(response);
    }
}

