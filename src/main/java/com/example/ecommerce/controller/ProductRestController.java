package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productRepository.findByNombreContainingIgnoreCase(search);
        } else {
            products = productRepository.findAll();
        }

        // Filtrar solo productos activos para usuarios normales
        products = products.stream()
                .filter(Product::isActivo)
                .collect(Collectors.toList());

        // Limitar resultados si se especifica
        if (limit != null && limit > 0 && limit < products.size()) {
            products = products.subList(0, limit);
        }

        System.out.println(">>> API /api/products devolviendo " + products.size() + " productos");
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
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

