package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register"; // Apunta a templates/register.html
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("usuario") Usuario usuario,
                               BindingResult result,
                               RedirectAttributes ra) {

        // Validación personalizada para evitar duplicados
        if (usuarioRepository.existsById(usuario.getCorreo())) {
            result.rejectValue("correo", "error.usuario", "Este correo electrónico ya está registrado.");
        }
        // Necesitamos un método para buscar por 'usuario' en el repositorio
        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            result.rejectValue("usuario", "error.usuario", "Este nombre de usuario ya está en uso.");
        }

        if (result.hasErrors()) {
            return "register";
        }

        // Encriptar contraseña y asignar rol por defecto
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setRoles("USER"); // Rol por defecto para nuevos registros

        usuarioRepository.save(usuario);

        ra.addFlashAttribute("successMessage", "¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }
}