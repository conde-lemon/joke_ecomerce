package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // Genera getters, setters, toString, etc.
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los argumentos (product, quantity)
public class CartItem {
    private Product product;
    private int quantity;

    public BigDecimal getSubtotal() {
        // Nos aseguramos de que el producto no sea nulo antes de calcular
        if (product == null || product.getPrecio() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrecio().multiply(BigDecimal.valueOf(quantity));
    }
}