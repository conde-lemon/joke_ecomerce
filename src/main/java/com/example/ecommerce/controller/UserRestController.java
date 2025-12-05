package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");
        return usuario != null && usuario.getRoles().contains("ADMIN");
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{correo}")
    public ResponseEntity<?> getUser(@PathVariable String correo, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return usuarioRepository.findByCorreo(correo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Usuario usuario, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            // Validar si el email ya existe
            if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El correo ya está registrado");
                return ResponseEntity.badRequest().body(error);
            }

            // Validar si el usuario ya existe
            if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El nombre de usuario ya está en uso");
                return ResponseEntity.badRequest().body(error);
            }

            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            usuario.setFechaCreacion(LocalDateTime.now());

            Usuario savedUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.ok(savedUsuario);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear usuario: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{correo}")
    public ResponseEntity<?> updateUser(
            @PathVariable String correo,
            @RequestBody Usuario usuario,
            HttpSession session
    ) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return usuarioRepository.findByCorreo(correo)
                .map(existingUsuario -> {
                    existingUsuario.setNombre(usuario.getNombre());
                    existingUsuario.setUsuario(usuario.getUsuario());
                    existingUsuario.setRoles(usuario.getRoles());

                    // Solo actualizar contraseña si se proporciona una nueva
                    if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
                        existingUsuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                    }

                    Usuario updated = usuarioRepository.save(existingUsuario);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{correo}")
    public ResponseEntity<?> deleteUser(@PathVariable String correo, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return usuarioRepository.findByCorreo(correo)
                .map(usuario -> {
                    usuarioRepository.delete(usuario);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Usuario eliminado exitosamente");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

