package com.example.ecommerce.controller;

import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class OrdersController {

    private final OrderService orderService;
    private final PedidoRepository pedidoRepository;

    // Este es el constructor original, sin el InvoiceService
    public OrdersController(OrderService orderService, PedidoRepository pedidoRepository) {
        this.orderService = orderService;
        this.pedidoRepository = pedidoRepository;
    }

    // Muestra la lista de pedidos del usuario
    @GetMapping("/orders")
    public String orders(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Proteger la ruta
        }
        List<Pedido> pedidos = pedidoRepository.findByUsuarioCorreoOrderByFechaPedidoDesc(principal.getName());
        model.addAttribute("pedidos", pedidos);
        return "orders"; // orders.html
    }


    @GetMapping("/orders/confirmation")
    public String orderConfirmation(@RequestParam("orderId") Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
 return "confirmation";
    }
}