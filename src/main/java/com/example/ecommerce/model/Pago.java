package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "metodo_pago", length = 50, nullable = false)
    private String metodoPago; // CARD, PAYPAL, CASH

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoPago estado;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    // Datos de tarjeta (encriptados en producción)
    @Column(name = "ultimos_4_digitos", length = 4)
    private String ultimos4Digitos;

    @Column(name = "titular_tarjeta", length = 100)
    private String titularTarjeta;

    // ID de transacción externa (PayPal, pasarela de pago, etc.)
    @Column(name = "transaccion_id", length = 100)
    private String transaccionId;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    public enum EstadoPago {
        PENDIENTE,
        APROBADO,
        RECHAZADO,
        REEMBOLSADO
    }
}

