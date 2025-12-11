package com.example.ecommerce.controller.rest;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UsuarioRepository usuarioRepository;

    public UserRestController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Endpoint para obtener TODOS los usuarios (acepta /api/usuarios y /api/users)
     * GET /api/usuarios
     */
    @GetMapping({"/usuarios", "/users"})
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint para obtener un usuario por correo (acepta /api/usuarios/{correo} y /api/users/{correo})
     * GET /api/usuarios/{correo}
     */
    @GetMapping({"/usuarios/{correo}", "/users/{correo}"})
    public ResponseEntity<?> obtenerUsuarioPorCorreo(@PathVariable String correo) {
        return usuarioRepository.findById(correo)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear nuevo usuario
     * POST /api/usuarios  o /api/users
     */
    @PostMapping({"/usuarios", "/users"})
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        if (usuario == null || usuario.getCorreo() == null) {
            return ResponseEntity.badRequest().body("Datos de usuario incompletos");
        }
        if (usuarioRepository.existsById(usuario.getCorreo())) {
            return ResponseEntity.status(409).body("Usuario ya existe");
        }
        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.ok(saved);
    }

    /**
     * Actualizar usuario existente (permitir cambiar roles, nombre, usuario, y contraseña opcionalmente)
     * PUT /api/usuarios/{correo}  o /api/users/{correo}
     */
    @PutMapping({"/usuarios/{correo}", "/users/{correo}"})
    public ResponseEntity<?> actualizarUsuario(@PathVariable String correo, @RequestBody Usuario cambios) {
        return usuarioRepository.findById(correo).map(existing -> {
            // Actualizar campos si vienen en el body (correo es clave primaria y no debe cambiar)
            if (cambios.getNombre() != null) existing.setNombre(cambios.getNombre());
            if (cambios.getUsuario() != null) existing.setUsuario(cambios.getUsuario());
            // Si contraseña viene vacía o null, no la cambiamos (lógica simple)
            if (cambios.getContrasena() != null && !cambios.getContrasena().isBlank()) {
                existing.setContrasena(cambios.getContrasena());
            }
            if (cambios.getRoles() != null) existing.setRoles(cambios.getRoles());
            Usuario saved = usuarioRepository.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar usuario
     * DELETE /api/usuarios/{correo}  o /api/users/{correo}
     */
    @DeleteMapping({"/usuarios/{correo}", "/users/{correo}"})
    public ResponseEntity<?> eliminarUsuario(@PathVariable String correo) {
        if (!usuarioRepository.existsById(correo)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(correo);
        return ResponseEntity.noContent().build();
    }
}
