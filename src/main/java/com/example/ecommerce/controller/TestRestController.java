package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class TestRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Probar autenticación actual
     */
    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> testAuth(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("currentUser");

        if (usuario == null) {
            response.put("authenticated", false);
            response.put("message", "No autenticado");
            response.put("sessionId", session.getId());
            return ResponseEntity.ok(response);
        }

        response.put("authenticated", true);
        response.put("user", Map.of(
                "correo", usuario.getCorreo(),
                "nombre", usuario.getNombre(),
                "usuario", usuario.getUsuario(),
                "roles", usuario.getRoles()
        ));
        response.put("sessionId", session.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Probar login manual
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> testLogin(
            @RequestBody Map<String, String> credentials,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        String email = credentials.get("email");
        String password = credentials.get("password");

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByCorreo(email).orElse(null);

        if (usuario == null) {
            response.put("success", false);
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.status(401).body(response);
        }

        // Verificar contraseña
        boolean passwordMatch = passwordEncoder.matches(password, usuario.getContrasena());

        response.put("passwordInDB", usuario.getContrasena().substring(0, 20) + "...");
        response.put("passwordMatch", passwordMatch);
        response.put("encoderType", passwordEncoder.getClass().getSimpleName());

        if (passwordMatch) {
            // Guardar en sesión
            session.setAttribute("currentUser", usuario);

            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("user", Map.of(
                    "correo", usuario.getCorreo(),
                    "nombre", usuario.getNombre(),
                    "roles", usuario.getRoles()
            ));
            response.put("sessionId", session.getId());

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Contraseña incorrecta");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Crear usuario de prueba con contraseña encriptada correctamente
     */
    @PostMapping("/create-user")
    public ResponseEntity<Map<String, Object>> createTestUser(
            @RequestBody Map<String, String> userData
    ) {
        Map<String, Object> response = new HashMap<>();

        String email = userData.get("email");
        String password = userData.get("password");
        String nombre = userData.get("nombre");
        String username = userData.get("username");
        String roles = userData.getOrDefault("roles", "USER");

        // Verificar si ya existe
        if (usuarioRepository.findByCorreo(email).isPresent()) {
            response.put("success", false);
            response.put("error", "El usuario ya existe");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setCorreo(email);
        usuario.setNombre(nombre);
        usuario.setUsuario(username);
        usuario.setContrasena(passwordEncoder.encode(password)); // Encriptar correctamente
        usuario.setRoles(roles);
        usuario.setFechaCreacion(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("message", "Usuario creado exitosamente");
        response.put("user", Map.of(
                "correo", usuario.getCorreo(),
                "nombre", usuario.getNombre(),
                "usuario", usuario.getUsuario(),
                "roles", usuario.getRoles(),
                "passwordHash", usuario.getContrasena().substring(0, 30) + "..."
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Verificar hash de contraseña
     */
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @RequestBody Map<String, String> data
    ) {
        Map<String, Object> response = new HashMap<>();

        String email = data.get("email");
        String password = data.get("password");

        Usuario usuario = usuarioRepository.findByCorreo(email).orElse(null);

        if (usuario == null) {
            response.put("found", false);
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.ok(response);
        }

        boolean matches = passwordEncoder.matches(password, usuario.getContrasena());

        response.put("found", true);
        response.put("email", usuario.getCorreo());
        response.put("passwordHash", usuario.getContrasena().substring(0, 30) + "...");
        response.put("matches", matches);
        response.put("encoderType", passwordEncoder.getClass().getSimpleName());

        if (!matches) {
            // Intentar generar el hash correcto para comparar
            String correctHash = passwordEncoder.encode(password);
            response.put("correctHash", correctHash.substring(0, 30) + "...");
            response.put("hint", "Si agregaste el usuario manualmente en la BD, la contraseña debe estar encriptada");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Info de sesión
     */
    @GetMapping("/session-info")
    public ResponseEntity<Map<String, Object>> sessionInfo(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        response.put("sessionId", session.getId());
        response.put("creationTime", session.getCreationTime());
        response.put("lastAccessedTime", session.getLastAccessedTime());
        response.put("maxInactiveInterval", session.getMaxInactiveInterval());
        response.put("isNew", session.isNew());

        Usuario usuario = (Usuario) session.getAttribute("currentUser");
        response.put("hasUser", usuario != null);

        if (usuario != null) {
            response.put("userEmail", usuario.getCorreo());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * TEST DE CONEXIÓN CON LA BASE DE DATOS
     */
    @GetMapping("/db-connection")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Contar usuarios
            long userCount = usuarioRepository.count();

            // Obtener información de la base de datos
            response.put("status", "SUCCESS");
            response.put("message", "Conexión exitosa con la base de datos");
            response.put("database", "MySQL");
            response.put("totalUsers", userCount);
            response.put("timestamp", LocalDateTime.now().toString());

            // Intentar obtener el primer usuario
            if (userCount > 0) {
                Usuario firstUser = usuarioRepository.findAll().stream().findFirst().orElse(null);
                if (firstUser != null) {
                    response.put("sampleUser", Map.of(
                        "correo", firstUser.getCorreo(),
                        "nombre", firstUser.getNombre(),
                        "roles", firstUser.getRoles()
                    ));
                }
            } else {
                response.put("warning", "No hay usuarios en la base de datos");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al conectar con la base de datos");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * INFORMACIÓN DEL SISTEMA Y BASE DE DATOS
     */
    @GetMapping("/system-info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Información de la aplicación
            response.put("application", "E-commerce Thymeleaf");
            response.put("version", "1.0.0");
            response.put("timestamp", LocalDateTime.now().toString());

            // Estadísticas de la base de datos
            Map<String, Object> dbStats = new HashMap<>();
            dbStats.put("totalUsers", usuarioRepository.count());

            response.put("databaseStats", dbStats);
            response.put("status", "RUNNING");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

