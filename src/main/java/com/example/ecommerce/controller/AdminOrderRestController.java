package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PedidoDTO;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:3001"}, allowCredentials = "true")
public class AdminOrderRestController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderRestController.class);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private MensajePedidoRepository mensajePedidoRepository;

    // Listar todos los pedidos
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listOrders() {
        try {
            logger.info("Solicitando lista de pedidos");
            List<Pedido> pedidos = pedidoRepository.findAll();
            logger.info("Se encontraron {} pedidos", pedidos.size());
            
            List<PedidoDTO> pedidosDTO = pedidos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(pedidosDTO);
        } catch (Exception e) {
            logger.error("Error al obtener pedidos: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Obtener mensajes de un pedido
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MensajePedido>> getOrderMessages(@PathVariable Long id) {
        List<MensajePedido> mensajes = mensajePedidoRepository.findByPedidoIdOrderByFechaMensajeDesc(id);
        return ResponseEntity.ok(mensajes);
    }
    
    private PedidoDTO convertToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId_pedido(pedido.getId());
        dto.setCorreo_usuario(pedido.getUsuario() != null ? pedido.getUsuario().getCorreo() : null);
        dto.setFecha_pedido(pedido.getFechaPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        return dto;
    }

    // Obtener un pedido por ID (incluye detalles y pagos si la entidad los carga por relaciones)
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getOrder(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener detalles de pedido
    @GetMapping("/{id}/details")
    public ResponseEntity<List<DetallePedido>> getOrderDetails(@PathVariable Long id) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(id);
        return ResponseEntity.ok(detalles);
    }

    // Obtener pagos del pedido
    @GetMapping("/{id}/payments")
    public ResponseEntity<List<Pago>> getOrderPayments(@PathVariable Long id) {
        List<Pago> pagos = pagoRepository.findByPedidoId(id);
        return ResponseEntity.ok(pagos);
    }

    // Actualizar estado del pedido con mensaje
    @PutMapping("/{id}/status")
    public ResponseEntity<Pedido> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("estado") String estado,
            @RequestParam(value = "mensaje", required = false) String mensaje
    ) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (!pedidoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Pedido pedido = pedidoOpt.get();
            EstadoPedido estadoAnterior = pedido.getEstado();
            EstadoPedido estadoNuevo = EstadoPedido.valueOf(estado.toUpperCase());
            
            pedido.setEstado(estadoNuevo);
            Pedido updated = pedidoRepository.save(pedido);
            
            // Guardar mensaje si se proporcionó
            if (mensaje != null && !mensaje.trim().isEmpty()) {
                MensajePedido mensajePedido = new MensajePedido();
                mensajePedido.setPedido(pedido);
                mensajePedido.setMensaje(mensaje.trim());
                mensajePedido.setEstadoAnterior(estadoAnterior);
                mensajePedido.setEstadoNuevo(estadoNuevo);
                mensajePedidoRepository.save(mensajePedido);
            }
            
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
