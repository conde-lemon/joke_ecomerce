package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.OrderForm;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@SessionAttributes({"cart", "orderForm"})
public class OrdersController {

    private final OrderService orderService;
    private final PedidoRepository pedidoRepository;

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

    // Muestra el formulario de checkout
    @GetMapping("/checkout")
    public String checkoutForm(@ModelAttribute("cart") Cart cart, Model model) {
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart"; // No se puede hacer checkout con carrito vacío
        }
        model.addAttribute("orderForm", new OrderForm());
        return "checkout"; // checkout.html
    }

    // Procesa el pedido
    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("orderForm") OrderForm form,
                                  BindingResult result,
                                  @ModelAttribute("cart") Cart cart,
                                  SessionStatus status,
                                  Principal principal,
                                  RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "checkout"; // Vuelve al formulario si hay errores
        }
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        if (principal == null) {
            return "redirect:/login"; // El usuario debe estar logueado
        }

        Pedido pedido = orderService.createOrder(cart, form, principal.getName());
        status.setComplete(); // Limpia el carrito y el form de la sesión

        ra.addFlashAttribute("msg", "¡Pedido realizado con éxito!");
        return "redirect:/orders/confirmation?orderId=" + pedido.getId();
    }

    // Muestra la página de confirmación
    @GetMapping("/orders/confirmation")
    public String orderConfirmation(@RequestParam("orderId") Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        // --- CORRECCIÓN ---
        // El archivo se llama 'confirmation.html', por lo que el nombre de la vista es 'confirmation'.
        return "confirmation";
    }
}