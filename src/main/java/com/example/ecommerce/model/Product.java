package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Producto")
    private Long id;

    @Column(name = "Nombre", length = 100)
    private String nombre;

    @Column(name = "Stock")
    private Integer stock;

    @Column(name = "Descripción", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "Precio", precision = 10, scale = 2)
    private BigDecimal precio;

    // --- CAMBIO: AÑADIMOS EL CAMPO PARA LA URL DE LA IMAGEN ---
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "Activo")
    private boolean activo;
}