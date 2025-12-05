package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository productRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(ProductRepository productRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showAdminDashboard() {
        return "admin-dashboard";
    }

    // --- GESTIÓN DE PRODUCTOS (sin cambios) ---
    @GetMapping("/products")
    public String showProductList(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin-products";
    }

    @GetMapping("/products/new")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto inválido:" + id));
        model.addAttribute("product", product);
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/product-form";
        }
        productRepository.save(product);
        ra.addFlashAttribute("successMessage", "¡Producto guardado con éxito!");
        return "redirect:/admin/products";
    }

    // --- GESTIÓN DE USUARIOS ---

    @GetMapping("/users")
    public String showUserList(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "admin-users";
    }

    // --- MÉTODOS PARA CREAR Y EDITAR USUARIOS ---

    @GetMapping("/users/new")
    public String showAdminCreateUserForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/user-form";
    }

    /**
     * CORRECCIÓN: Añadimos el método para manejar la edición de usuarios.
     * La expresión regular ':.+' es necesaria para que Spring no trunque el email en el último punto.
     */
    @GetMapping("/users/edit/{email:.+}")
    public String showAdminEditUserForm(@PathVariable("email") String email, Model model) {
        Usuario usuario = usuarioRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Email de usuario inválido: " + email));

        // Se envía la contraseña vacía al formulario por seguridad.
        usuario.setContrasena("");
        model.addAttribute("usuario", usuario);
        return "admin/user-form";
    }

    /**
     * MEJORA: El método de guardado ahora maneja tanto la creación como la edición.
     */
    @PostMapping("/users/save")
    public String saveUserByAdmin(@Valid @ModelAttribute("usuario") Usuario usuario,
                                  BindingResult result,
                                  RedirectAttributes ra) {

        // Determinar si es una creación o una actualización
        boolean isNewUser = usuarioRepository.findById(usuario.getCorreo()).isEmpty();

        // 1. Validación de nombre de usuario duplicado (solo si es nuevo o si el nombre de usuario ha cambiado)
        usuarioRepository.findByUsuario(usuario.getUsuario()).ifPresent(existingByUsername -> {
            if (isNewUser || !existingByUsername.getCorreo().equals(usuario.getCorreo())) {
                result.rejectValue("usuario", "error.usuario", "Este nombre de usuario ya está en uso.");
            }
        });

        // 2. Validación de contraseña para nuevos usuarios
        if (isNewUser && (usuario.getContrasena() == null || usuario.getContrasena().isBlank())) {
            result.rejectValue("contrasena", "error.usuario", "La contraseña es obligatoria para nuevos usuarios.");
        }

        if (result.hasErrors()) {
            return "admin/user-form";
        }

        // 3. Lógica de la contraseña
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            // Si se proporcionó una nueva contraseña, se encripta.
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        } else {
            // Si no se proporcionó contraseña (solo en modo edición), se mantiene la antigua.
            if (!isNewUser) {
                // Obtenemos la contraseña encriptada ya existente en la BD
                String oldPassword = usuarioRepository.findById(usuario.getCorreo()).get().getContrasena();
                usuario.setContrasena(oldPassword);
            }
        }

        usuarioRepository.save(usuario);

        ra.addFlashAttribute("successMessage", "Usuario '" + usuario.getNombre() + "' guardado con éxito.");
        return "redirect:/admin/users";
    }
}