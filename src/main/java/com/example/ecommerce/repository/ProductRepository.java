package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca productos por nombre (que contenga 'q') y un rango de precios.
     * Esta consulta se ejecuta directamente en la base de datos.
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(:q IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
            "(:min IS NULL OR p.precio >= :min) AND " +
            "(:max IS NULL OR p.precio <= :max) " +
            "ORDER BY p.nombre ASC")
    List<Product> search(@Param("q") String q, @Param("min") BigDecimal min, @Param("max") BigDecimal max);

    /**
     * Busca productos por nombre que contenga el texto especificado (case insensitive).
     * Método derivado de Spring Data JPA.
     */
    List<Product> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca todos los productos activos.
     * Método derivado de Spring Data JPA.
     */
    List<Product> findByActivoTrue();

    /**
     * Busca todos los productos inactivos.
     * Método derivado de Spring Data JPA.
     */
    List<Product> findByActivoFalse();

    /**
     * Busca productos por estado activo.
     * Método derivado de Spring Data JPA.
     */
    List<Product> findByActivo(boolean activo);
}