package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; // Importar PathVariable
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository productRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminController(ProductRepository productRepository, UsuarioRepository usuarioRepository) {
        this.productRepository = productRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String showAdminDashboard() {
        return "admin-dashboard";
    }

    // --- GESTIÓN DE PRODUCTOS ---

    @GetMapping("/products")
    public String showProductList(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin-products";
    }

    /**
     * Muestra el formulario para CREAR un nuevo producto.
     */
    @GetMapping("/products/new")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        // Asegúrate de que tu vista esté en 'templates/admin/product-form.html'
        return "admin/product-form";
    }

    // --- NUEVO MÉTODO PARA EDITAR ---
    /**
     * Muestra el formulario para EDITAR un producto existente.
     * @param id El ID del producto a editar.
     */
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model) {
        // Buscamos el producto en la base de datos
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto inválido:" + id));

        // Pasamos el producto encontrado a la vista
        model.addAttribute("product", product);
        // Reutilizamos la misma vista del formulario
        return "admin/product-form";
    }
    // --- FIN DEL NUEVO MÉTODO ---

    /**
     * Procesa el guardado, tanto para productos nuevos como para existentes.
     */
    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            // Si hay errores, volvemos al formulario (mantendrá los datos)
            return "admin/product-form";
        }
        // El método save() de JPA crea si el ID es nulo, y actualiza si el ID ya existe.
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
}