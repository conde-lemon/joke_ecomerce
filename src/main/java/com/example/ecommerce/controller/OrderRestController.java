package com.example.ecommerce.controller;

import com.example.ecommerce.model.OrderForm;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> getUserOrders(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Pedido> pedidos = pedidoRepository.findByUsuarioCorreoOrderByFechaPedidoDesc(usuario.getCorreo());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return pedidoRepository.findById(id)
                .filter(pedido -> pedido.getUsuario().getCorreo().equals(usuario.getCorreo()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody(required = false) Map<String, Object> orderData,
            HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Obtener el carrito de la sesión
            var cart = cartService.getCart();

            if (cart.getItems().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El carrito está vacío");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear el pedido
            OrderForm orderForm = new OrderForm();
            Pedido pedido = orderService.createOrder(cart, orderForm, usuario.getCorreo());

            // Limpiar el carrito después de crear el pedido
            cartService.clear();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pedido creado exitosamente");
            response.put("id", pedido.getId());
            response.put("status", "PENDING");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear pedido: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
            @RequestBody(required = false) OrderForm orderForm,
            HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Si no se proporciona orderForm, crear uno simple
            if (orderForm == null) {
                orderForm = new OrderForm();
            }

            // Obtener el carrito de la sesión
            var cart = cartService.getCart();

            if (cart.getItems().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El carrito está vacío");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear el pedido
            Pedido pedido = orderService.createOrder(cart, orderForm, usuario.getCorreo());

            // Limpiar el carrito después de crear el pedido
            cartService.clear();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pedido creado exitosamente");
            response.put("pedidoId", pedido.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear pedido: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null || !usuario.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Pedido> pedidos = pedidoRepository.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null || !usuario.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return pedidoRepository.findById(id)
                .map(pedido -> {
                    // Aquí podrías actualizar el estado del pedido
                    // pedido.setEstado(EstadoPedido.valueOf(request.get("estado")));
                    // pedidoRepository.save(pedido);
                    return ResponseEntity.ok(pedido);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

