package com.example.ecommerce.repository;

import com.example.ecommerce.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPedidoId(Long pedidoId);

    Optional<Pago> findByTransaccionId(String transaccionId);

    List<Pago> findByEstado(Pago.EstadoPago estado);
}

