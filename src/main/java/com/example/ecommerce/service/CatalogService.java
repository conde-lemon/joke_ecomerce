package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final ProductRepository productRepository;

    // Inyectamos el repositorio para poder hablar con la base de datos
    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Devuelve todos los productos desde la base de datos.
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Busca un producto por su ID en la base de datos.
     */
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Búsqueda por nombre y/o precio, delegando al repositorio.
     */
    public List<Product> search(String q, BigDecimal min, BigDecimal max) {
        // Si la query 'q' está vacía, la tratamos como nula para que la consulta funcione
        String query = (q != null && q.trim().isEmpty()) ? null : q;
        return productRepository.search(query, min, max);
    }
}