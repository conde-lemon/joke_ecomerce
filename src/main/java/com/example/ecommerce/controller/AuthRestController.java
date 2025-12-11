package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> credentials,
            HttpSession session
    ) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Usuario usuario = usuarioRepository.findByCorreo(email).orElse(null);

        // Comparación simple de contraseñas (sin encriptación)
        if (usuario == null || !usuario.getContrasena().equals(password)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Guardar usuario en sesión
        session.setAttribute("currentUser", usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login exitoso");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("correo", usuario.getCorreo());
        userInfo.put("nombre", usuario.getNombre());
        userInfo.put("usuario", usuario.getUsuario());
        userInfo.put("roles", usuario.getRoles());

        response.put("user", userInfo);
        response.put("token", session.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Usuario usuario) {
        try {
            // Validar si el email ya existe
            if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El correo ya está registrado");
                return ResponseEntity.badRequest().body(error);
            }

            // Validar si el usuario ya existe
            if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El nombre de usuario ya está en uso");
                return ResponseEntity.badRequest().body(error);
            }

            // Guardar contraseña SIN encriptar
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setRoles("USER");

            Usuario savedUsuario = usuarioRepository.save(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("correo", savedUsuario.getCorreo());
            userInfo.put("nombre", savedUsuario.getNombre());
            userInfo.put("usuario", savedUsuario.getUsuario());

            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("correo", usuario.getCorreo());
        userInfo.put("nombre", usuario.getNombre());
        userInfo.put("usuario", usuario.getUsuario());
        userInfo.put("roles", usuario.getRoles());
        userInfo.put("fechaCreacion", usuario.getFechaCreacion());

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");

        return ResponseEntity.ok(response);
    }
}
