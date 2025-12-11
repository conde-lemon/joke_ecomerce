package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

    // DESHABILITADO: Ahora usamos DataLoader.java para cargar datos de prueba
    // Este método creaba usuarios con contraseñas sin encriptar
    /*
    @Bean
    CommandLineRunner initUsers(UsuarioRepository usuarioRepository) {
        return args -> {
            // Crear usuario admin si no existe
            if (usuarioRepository.findByCorreo("admin@example.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setCorreo("admin@example.com");
                admin.setUsuario("admin");
                admin.setNombre("Administrador");
                admin.setContrasena("admin123");  // Contraseña sin encriptar
                admin.setRoles("USER,ADMIN");
                admin.setFechaCreacion(LocalDateTime.now());
                usuarioRepository.save(admin);
                System.out.println("✅ Usuario admin creado: admin@example.com / admin123");
            }

            // Crear usuario normal si no existe
            if (usuarioRepository.findByCorreo("user@example.com").isEmpty()) {
                Usuario user = new Usuario();
                user.setCorreo("user@example.com");
                user.setUsuario("usuario");
                user.setNombre("Usuario Normal");
                user.setContrasena("user123");  // Contraseña sin encriptar
                user.setRoles("USER");
                user.setFechaCreacion(LocalDateTime.now());
                usuarioRepository.save(user);
                System.out.println("✅ Usuario normal creado: user@example.com / user123");
            }
        };
    }
    */
}

