package com.example.ecommerce.controller;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import com.example.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Mostrar página de checkout
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model, Principal principal, HttpSession session) {
        // Verificar autenticación
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // Obtener carrito
            var cart = cartService.getCart();

            if (cart == null || cart.getItems().isEmpty()) {
                return "redirect:/cart";
            }

            // Agregar datos al modelo
            model.addAttribute("cart", cart);
            model.addAttribute("orderForm", new OrderForm());

            // Pre-llenar con datos del usuario si existen
            Usuario usuario = (Usuario) session.getAttribute("currentUser");
            if (usuario != null && usuario.getCorreo() != null) {
                OrderForm form = new OrderForm();
                form.setEmail(usuario.getCorreo());
                form.setFirstName(usuario.getNombre() != null ? usuario.getNombre().split(" ")[0] : "");
                model.addAttribute("orderForm", form);
            }

            return "checkout";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar checkout: " + e.getMessage());
            return "checkout";
        }
    }

    /**
     * Procesar checkout - IMPORTANTE: Captura errores sin perder sesión
     */
    @PostMapping("/checkout")
    public String processCheckout(
            @Valid @ModelAttribute("orderForm") OrderForm orderForm,
            BindingResult result,
            Model model,
            Principal principal,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Verificar autenticación SIN perder sesión
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión");
            return "redirect:/login";
        }

        try {
            // Si hay errores de validación, volver al formulario SIN perder sesión
            if (result.hasErrors()) {
                var cart = cartService.getCart();
                model.addAttribute("cart", cart);
                model.addAttribute("error", "Por favor corrige los errores en el formulario");
                return "checkout"; // NO redirect, devuelve la vista directamente
            }

            // Obtener usuario desde la sesión PRIMERO (más seguro)
            Usuario usuario = (Usuario) session.getAttribute("currentUser");

            // Si no está en sesión, buscar en BD
            if (usuario == null) {
                String username = principal.getName();
                usuario = usuarioRepository.findByCorreo(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Guardar en sesión para próximas peticiones
                session.setAttribute("currentUser", usuario);
            }

            // Obtener carrito
            var cart = cartService.getCart();

            if (cart == null || cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío");
                return "redirect:/cart";
            }

            // Crear pedido
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setEstado(EstadoPedido.PENDIENTE);

            BigDecimal total = BigDecimal.ZERO;

            // Agregar detalles del pedido
            for (var item : cart.getItems()) {
                if (item.getProduct() == null) {
                    continue;
                }

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

            // Guardar pedido
            pedido = pedidoRepository.save(pedido);

            // Limpiar carrito SOLO si el pedido se guardó exitosamente
            cartService.clear();

            // Guardar ID del pedido en sesión para la confirmación
            session.setAttribute("lastOrderId", pedido.getId());

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Pedido creado exitosamente");
            redirectAttributes.addFlashAttribute("orderId", pedido.getId());

            return "redirect:/confirmation";

        } catch (Exception e) {
            // IMPORTANTE: Capturar errores SIN perder sesión
            e.printStackTrace();

            // Recargar el carrito para mostrar el formulario de nuevo
            try {
                var cart = cartService.getCart();
                model.addAttribute("cart", cart);
            } catch (Exception ex) {
                // Si falla recargar el carrito, al menos mostrar el formulario
            }

            model.addAttribute("error", "Error al procesar el pedido: " + e.getMessage());
            model.addAttribute("orderForm", orderForm); // Mantener los datos ingresados

            // Devolver la vista directamente (NO redirect) para no perder la sesión
            return "checkout";
        }
    }

    /**
     * Página de confirmación
     */
    @GetMapping("/confirmation")
    public String showConfirmation(Model model, HttpSession session, Principal principal) {
        // Verificar autenticación
        if (principal == null) {
            return "redirect:/login";
        }

        // Obtener el ID del último pedido de la sesión
        Long orderId = (Long) session.getAttribute("lastOrderId");

        if (orderId != null) {
            model.addAttribute("orderId", orderId);
            // Limpiar después de mostrar
            session.removeAttribute("lastOrderId");
        }

        return "confirmation";
    }
}

