package com.example.ecommerce.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@Controller
public class TestOrdersController {

    private final JdbcTemplate jdbcTemplate;

    public TestOrdersController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Página Thymeleaf para inspección manual (acepta ?email=...)
    @GetMapping("/test/orders")
    public String viewTestOrders(@RequestParam(required = false) String email, Model model) {
        try {
            // Si no se pasó email, intentar obtener del usuario autenticado
            if ((email == null || email.isBlank())) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof UserDetails) {
                        email = ((UserDetails) principal).getUsername();
                    } else if (principal instanceof String) {
                        // en algunos setups principal es username String
                        email = (String) principal;
                    }
                }
            }

            List<Map<String, Object>> orders;
            if (email != null && !email.isBlank()) {
                orders = jdbcTemplate.queryForList(
                    "SELECT id_pedido AS id, estado, fecha_pedido AS fechaPedido, total FROM pedido WHERE correo_usuario = ? ORDER BY id_pedido DESC",
                    email
                );
            } else {
                // si no hay email, devolver lista vacía para evitar mostrar todos los pedidos por defecto
                orders = List.of();
            }

            model.addAttribute("orders", orders);
            model.addAttribute("count", orders.size());
            model.addAttribute("emailFilter", email != null ? email : "");
        } catch (DataAccessException ex) {
            model.addAttribute("orders", List.of());
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("count", 0);
            model.addAttribute("emailFilter", email != null ? email : "");
        }
        return "test-orders";
    }

    // API JSON para que el frontend pruebe la conexión (acepta ?email=...)
    @GetMapping("/api/test/orders")
    @ResponseBody
    public ResponseEntity<?> apiTestOrders(@RequestParam(required = false) String email) {
        try {
            // Si no se pasó email, intentar obtener del usuario autenticado
            if ((email == null || email.isBlank())) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof UserDetails) {
                        email = ((UserDetails) principal).getUsername();
                    } else if (principal instanceof String) {
                        email = (String) principal;
                    }
                }
            }

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Se requiere parámetro email o usuario autenticado"));
            }

            List<Map<String, Object>> orders = jdbcTemplate.queryForList(
                "SELECT id_pedido AS id, estado, fecha_pedido AS fechaPedido, total FROM pedido WHERE correo_usuario = ? ORDER BY id_pedido DESC",
                email
            );
            return ResponseEntity.ok(orders);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error consultando pedidos", "detail", ex.getMessage()));
        }
    }
}
