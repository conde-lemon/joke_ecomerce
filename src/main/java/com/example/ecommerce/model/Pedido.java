package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
// --- MEJORA 1: Reemplazar @Data por anotaciones específicas ---
@Getter
@Setter
@ToString(exclude = "detalles") // Excluimos la colección para evitar bucles infinitos
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido") // Sugerencia: usar snake_case para consistencia (id_pedido)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "correo_usuario", referencedColumnName = "correo")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPedido estado;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    // --- MEJORA 2: Método de ayuda para sincronizar la relación ---
    public void addDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this); // Sincroniza el otro lado de la relación
    }

    public void removeDetalle(DetallePedido detalle) {
        detalles.remove(detalle);
        detalle.setPedido(null);
    }
}