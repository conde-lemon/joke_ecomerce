package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository;
    private final UsuarioRepository usuarioRepository;

    public OrderService(PedidoRepository pedidoRepository, ProductRepository productRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional // Asegura que toda la operación se complete o se revierta
    public Pedido createOrder(Cart cart, OrderForm orderForm, String userEmail) {
        // 1. Buscar el usuario que realiza el pedido
        Usuario usuario = usuarioRepository.findById(userEmail)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado para crear el pedido."));

        // 2. Crear el objeto Pedido principal
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(cart.getTotal());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        // 3. Crear los detalles del pedido a partir del carrito
        for (CartItem item : cart.getItems()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(item.getProduct());
            detalle.setCantidad(item.getQuantity());
            detalle.setPrecioUnitario(item.getProduct().getPrecio());
            detalle.setSubtotal(item.getSubtotal());
            pedido.getDetalles().add(detalle);

            // 4. (Opcional pero recomendado) Actualizar el stock del producto
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException("Producto del carrito no encontrado en la BD."));

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("No hay suficiente stock para el producto: " + product.getNombre());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // 5. Guardar el pedido y sus detalles en la base de datos
        return pedidoRepository.save(pedido);
    }
}