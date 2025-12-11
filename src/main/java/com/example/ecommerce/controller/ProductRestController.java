package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Obtener todos los productos activos.
     * Endpoint público accesible sin autenticación.
     *
     * @param search Filtrar por nombre (opcional)
     * @param limit Limitar cantidad de resultados (opcional)
     * @return Lista de productos activos en formato JSON
     */
    @GetMapping
    @ResponseBody
    public List<Product> getAllProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "onlyInStock", required = false) Boolean onlyInStock
    ) {
        List<Product> products = productRepository.findAll();

        // Filtrar solo productos activos
        products = products.stream()
                .filter(Product::isActivo)
                .collect(Collectors.toList());

        // Aplicar filtros
        if (search != null && !search.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrecio().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrecio().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        if (onlyInStock != null && onlyInStock) {
            products = products.stream()
                    .filter(p -> p.getStock() > 0)
                    .collect(Collectors.toList());
        }

        // Aplicar ordenamiento
        if (sort != null) {
            switch (sort) {
                case "precio_asc":
                    products.sort(Comparator.comparing(Product::getPrecio));
                    break;
                case "precio_desc":
                    products.sort(Comparator.comparing(Product::getPrecio).reversed());
                    break;
                case "nombre_asc":
                    products.sort(Comparator.comparing(Product::getNombre));
                    break;
                case "nombre_desc":
                    products.sort(Comparator.comparing(Product::getNombre).reversed());
                    break;
                case "stock_desc":
                    products.sort(Comparator.comparing(Product::getStock).reversed());
                    break;
            }
        }

        // Limitar resultados si se especifica
        if (limit != null && limit > 0 && limit < products.size()) {
            products = products.subList(0, limit);
        }

        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setNombre(product.getNombre());
                    existingProduct.setDescripcion(product.getDescripcion());
                    existingProduct.setPrecio(product.getPrecio());
                    existingProduct.setStock(product.getStock());
                    existingProduct.setImageUrl(product.getImageUrl());
                    existingProduct.setActivo(product.isActivo());
                    Product updated = productRepository.save(existingProduct);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    // Soft delete: marcar como inactivo en lugar de borrar físicamente
                    product.setActivo(false);
                    productRepository.save(product);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
