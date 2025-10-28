package com.example.ecommerce.repository;

import com.example.ecommerce.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Método para buscar todos los pedidos de un usuario por su correo
    List<Pedido> findByUsuarioCorreoOrderByFechaPedidoDesc(String correo);
}