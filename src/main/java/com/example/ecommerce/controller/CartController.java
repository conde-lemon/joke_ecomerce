package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart") // El atributo de sesión ahora es un objeto Cart
public class CartController {

    private final CatalogService catalog;

    public CartController(CatalogService catalog) {
        this.catalog = catalog;
    }

    /** Crea el objeto Cart en sesión si no existe */
    @ModelAttribute("cart")
    public Cart cart() {
        return new Cart();
    }

    /** Agregar producto al carrito */
    @PostMapping("/add/{id}")
    public String add(@PathVariable("id") Long id,
                      @RequestParam(name = "qty", defaultValue = "1") int qty,
                      @ModelAttribute("cart") Cart cart, // Usamos el objeto Cart
                      RedirectAttributes ra) {

        Product p = catalog.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        cart.add(p, qty);

        // Usamos getNombre() en lugar de getName()
        ra.addFlashAttribute("msg", "Agregado: " + p.getNombre() + " x" + qty);
        return "redirect:/cart";
    }

    /** Ver carrito */
    @GetMapping
    public String view(@ModelAttribute("cart") Cart cart, Model model) {
        // Pasamos el objeto cart completo a la vista.
        // Thymeleaf puede llamar a cart.getItems(), cart.getSubtotal(), etc.
        model.addAttribute("cart", cart);
        return "cart"; // templates/cart.html
    }

    /** Actualizar cantidad (si qty <= 0, se elimina) */
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestParam(name = "qty") int qty,
                         @ModelAttribute("cart") Cart cart) {
        // La lógica ahora está en el objeto Cart
        cart.updateQuantity(id, qty);
        return "redirect:/cart";
    }

    /** Quitar una línea del carrito */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id,
                         @ModelAttribute("cart") Cart cart) {
        cart.remove(id);
        return "redirect:/cart";
    }

    /** Vaciar carrito y limpiar la sesión */
    @PostMapping("/clear")
    public String clear(SessionStatus status) {
        // setComplete() elimina el atributo "cart" de la sesión
        status.setComplete();
        return "redirect:/cart";
    }
}