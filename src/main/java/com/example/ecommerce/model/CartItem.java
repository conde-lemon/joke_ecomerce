// src/main/java/com/example/ecommerce/model/CartItem.java
package com.example.ecommerce.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItem {

    private Product product;
    private int quantity; // El campo se llama 'quantity'

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Calcula el subtotal para esta línea del carrito.
     * @return El precio del producto multiplicado por la cantidad.
     */
    public BigDecimal getSubtotal() {
        if (product == null || product.getPrecio() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrecio().multiply(new BigDecimal(quantity));
    }

    /**
     * Devuelve el precio unitario del producto.
     * @return El precio del producto.
     */
    public BigDecimal getUnitPrice() {
        return product != null ? product.getPrecio() : BigDecimal.ZERO;
    }
}