package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PedidoUsuarioDTO;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
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

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MensajePedidoRepository mensajePedidoRepository;

    @GetMapping
    public ResponseEntity<?> getUserOrders(
            HttpSession session,
            @RequestParam(value = "email", required = false) String email
    ) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");
        
        // Si no hay usuario en sesión pero se proporciona email, buscar por email
        if (usuario == null && email != null) {
            usuario = usuarioRepository.findByCorreo(email).orElse(null);
        }

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Pedido> pedidos = pedidoRepository.findByUsuarioCorreoOrderByFechaPedidoDesc(usuario.getCorreo());
        
        // Convertir a DTO con mensajes
        List<PedidoUsuarioDTO> pedidosDTO = pedidos.stream()
            .map(this::convertToPedidoUsuarioDTO)
            .toList();
            
        return ResponseEntity.ok(pedidosDTO);
    }
    
    private PedidoUsuarioDTO convertToPedidoUsuarioDTO(Pedido pedido) {
        PedidoUsuarioDTO dto = new PedidoUsuarioDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setMensajes(mensajePedidoRepository.findByPedidoIdOrderByFechaMensajeDesc(pedido.getId()));
        return dto;
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            // Recargar el usuario desde la BD para evitar problemas de sesión
            usuario = usuarioRepository.findByCorreo(usuario.getCorreo())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener el carrito de la sesión
            var cart = cartService.getCart();

            if (cart == null || cart.getItems().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El carrito está vacío");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear el pedido manualmente para evitar problemas de sesión
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setEstado(EstadoPedido.PENDIENTE);

            BigDecimal total = BigDecimal.ZERO;
            for (var item : cart.getItems()) {
                BigDecimal subtotal = item.getProduct().getPrecio()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(subtotal);

                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(item.getProduct());
                detalle.setCantidad(item.getQuantity());
                detalle.setPrecioUnitario(item.getProduct().getPrecio());
                detalle.setSubtotal(subtotal);
                pedido.addDetalle(detalle);
            }

            pedido.setTotal(total);
            pedido = pedidoRepository.save(pedido);

            // Limpiar el carrito después de crear el pedido
            cartService.clear();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pedido creado exitosamente");
            response.put("id", pedido.getId());
            response.put("status", "PENDIENTE");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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

