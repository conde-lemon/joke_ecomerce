package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
// CORRECCIÓN 1: Apuntamos a la tabla "usuarios" en plural, como debe ser.
@Table(name = "usuarios")
// MEJORA: Reemplazamos @Data por anotaciones específicas para evitar problemas con JPA.
@Getter
@Setter
@ToString
public class Usuario {

    @Id
    @Column(length = 100)
    private String correo;

    @Column(length = 50, unique = true)
    private String usuario;

    @Column(length = 100)
    private String nombre;

    // Este es el único campo para la contraseña, lo cual es correcto.
    @Column(length = 255)
    private String contrasena;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Campo para los roles del usuario (ej: "USER,ADMIN")
    private String roles;
}