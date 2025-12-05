package com.example.ecommerce;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

    // --- MÉTODO ACTUALIZADO PARA CREAR USUARIOS ---
    @Bean
    @Order(1)
    public CommandLineRunner loadUsers(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear usuario normal
            createUserIfNotFound(repository, passwordEncoder,
                    "user@example.com",
                    "user",
                    "Usuario de Prueba",
                    "password",
                    "USER");

            // Crear usuario administrador
            createUserIfNotFound(repository, passwordEncoder,
                    "admin@example.com",
                    "admin",
                    "Administrador del Sistema",
                    "admin",
                    "USER,ADMIN"); // Roles separados por coma
        };
    }

    /**
     * Método de ayuda para crear un usuario si no existe en la base de datos.
     */
    private void createUserIfNotFound(UsuarioRepository repository, PasswordEncoder passwordEncoder,
                                      String email, String username, String name, String rawPassword, String roles) {
        if (repository.findById(email).isEmpty()) {
            System.out.println(">>> Creando usuario: " + email);
            Usuario newUser = new Usuario();
            newUser.setCorreo(email);
            newUser.setUsuario(username);
            newUser.setNombre(name);
            // Encriptamos la contraseña antes de guardarla
            newUser.setContrasena(passwordEncoder.encode(rawPassword));
            newUser.setRoles(roles);
            repository.save(newUser);
            System.out.println(">>> Usuario '" + email + "' guardado con roles: " + roles);
        } else {
            System.out.println(">>> El usuario '" + email + "' ya existe.");
        }
    }

    // ... (El resto de la clase: loadProducts, loadOrders, createProduct se quedan igual) ...
    @Bean
    @Order(2)
    public CommandLineRunner loadProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                System.out.println(">>> Creando 20 productos de prueba...");

                List<Product> products = new ArrayList<>();
                products.add(createProduct("Laptop Pro 15", 50, "Una laptop potente para profesionales.", "1500.99", "/img/p1.jpg", true));
                products.add(createProduct("Monitor UltraWide 34\"", 30, "Monitor curvo para una inmersión total.", "750.50", "/img/p2.jpg", true));
                products.add(createProduct("Teclado Mecánico RGB", 100, "Teclado con switches Cherry MX.", "120.00", "/img/p3.jpg", true));
                products.add(createProduct("Mouse Inalámbrico Ergo", 200, "Mouse ergonómico con conexión Bluetooth.", "25.50", "/img/p4.jpg", true));
                products.add(createProduct("Webcam 4K Pro", 80, "Cámara web con resolución 4K y autoenfoque.", "199.99", "/img/p5.jpg", true));
                products.add(createProduct("Smartphone X12", 150, "El último modelo con cámara triple.", "999.90", "/img/p6.jpg", true));
                products.add(createProduct("Auriculares con Cancelación de Ruido", 120, "Sumérgete en tu música sin distracciones.", "250.00", "/img/p7.jpg", true));
                products.add(createProduct("Cargador Rápido USB-C 65W", 300, "Carga tus dispositivos a máxima velocidad.", "45.00", "/img/p8.jpg", true));
                products.add(createProduct("Smartwatch Fit 3", 90, "Monitoriza tu actividad física y salud.", "180.75", "/img/p9.jpg", true));
                products.add(createProduct("Cafetera Express Automática", 40, "Prepara café de especialidad en casa.", "350.00", "/img/p10.jpg", true));
                products.add(createProduct("Robot Aspirador Inteligente", 25, "Limpia tu casa mientras no estás.", "450.00", "/img/p11.jpg", true));
                products.add(createProduct("Freidora de Aire 5L", 60, "Cocina saludable sin aceite.", "99.99", "/img/p12.jpg", true));
                products.add(createProduct("Libro: El Arte de Programar", 500, "Un clásico de la literatura informática.", "35.90", "/img/p13.jpg", true));
                products.add(createProduct("Libro: Clean Code", 400, "Guía para producir código legible y mantenible.", "42.50", "/img/p14.jpg", true));
                products.add(createProduct("Zapatillas Urbanas", 80, "Comodidad y estilo para tu día a día.", "89.99", "/img/p15.jpg", true));
                products.add(createProduct("Mochila Antirrobo", 110, "Protege tus pertenencias con estilo.", "75.00", "/img/p16.jpg", true));
                products.add(createProduct("Gafas de Sol Polarizadas", 200, "Protección UV400 y diseño moderno.", "55.20", "/img/p17.jpg", true));
                products.add(createProduct("Botella de Agua Reutilizable", 300, "Mantente hidratado y cuida el planeta.", "19.99", "/img/p18.jpg", true));
                products.add(createProduct("Batería Externa 20000mAh", 150, "Nunca te quedes sin batería.", "39.95", "/img/p19.jpg", true));
                products.add(createProduct("Luz de Escritorio LED", 90, "Iluminación ajustable para tu espacio de trabajo.", "29.99", "/img/p20.jpg", true));

                productRepository.saveAll(products);
                System.out.println(">>> 20 productos de prueba guardados.");
            } else {
                System.out.println(">>> Ya existen productos en la base de datos.");
            }
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner loadOrders(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, ProductRepository productRepository) {
        return args -> {
            if (pedidoRepository.count() == 0 && productRepository.count() > 0) {
                System.out.println(">>> Creando pedido de prueba...");

                Usuario usuario = usuarioRepository.findById("user@example.com").orElse(null); // Cambiado de test@example.com
                Product p1 = productRepository.findById(1L).orElse(null);
                Product p4 = productRepository.findById(4L).orElse(null);

                if (usuario != null && p1 != null && p4 != null) {
                    Pedido pedido1 = new Pedido();
                    pedido1.setUsuario(usuario);
                    pedido1.setEstado(EstadoPedido.ENTREGADO);

                    DetallePedido detalle1 = new DetallePedido();
                    detalle1.setProducto(p1);
                    detalle1.setCantidad(1);
                    detalle1.setPrecioUnitario(p1.getPrecio());
                    detalle1.setSubtotal(p1.getPrecio().multiply(new BigDecimal(1)));
                    pedido1.addDetalle(detalle1);

                    DetallePedido detalle2 = new DetallePedido();
                    detalle2.setProducto(p4);
                    detalle2.setCantidad(2);
                    detalle2.setPrecioUnitario(p4.getPrecio());
                    detalle2.setSubtotal(p4.getPrecio().multiply(new BigDecimal(2)));
                    pedido1.addDetalle(detalle2);

                    BigDecimal totalPedido = detalle1.getSubtotal().add(detalle2.getSubtotal());
                    pedido1.setTotal(totalPedido);

                    pedidoRepository.save(pedido1);
                    System.out.println(">>> Pedido de prueba guardado.");
                }
            } else {
                System.out.println(">>> Ya existen pedidos en la base de datos.");
            }
        };
    }

    private Product createProduct(String nombre, int stock, String descripcion, String precio, String imageUrl, boolean activo) {
        Product p = new Product();
        p.setNombre(nombre);
        p.setStock(stock);
        p.setDescripcion(descripcion);
        p.setPrecio(new BigDecimal(precio));
        p.setImageUrl(imageUrl);
        p.setActivo(activo);
        return p;
    }
}