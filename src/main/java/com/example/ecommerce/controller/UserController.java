package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UserController {

    private final UsuarioRepository usuarioRepository;

    public UserController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Muestra la página de perfil del usuario actualmente autenticado.
     */
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        // Principal.getName() nos da el username (email) del usuario logueado
        String email = principal.getName();

        // Buscamos al usuario en la base de datos
        Usuario usuario = usuarioRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Pasamos el objeto Usuario a la vista
        model.addAttribute("usuario", usuario);

        return "user-profile"; // Renderiza la vista user-profile.html
    }
}