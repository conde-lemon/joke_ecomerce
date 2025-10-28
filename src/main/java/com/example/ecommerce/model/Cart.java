package com.example.ecommerce.model;

import java.math.BigDecimal;
import java.util.*;

public class Cart {
    private final Map<Long, CartItem> items = new LinkedHashMap<>();
    // Costo de envío fijo, puedes hacerlo más dinámico en el futuro
    private final BigDecimal shipping = new BigDecimal("10.00");

    public void add(Product p, int qty) {
        if (p == null || p.getId() == null) {
            return; // No agregar productos nulos
        }
        int quantityToAdd = Math.max(qty, 1);
        CartItem existing = items.get(p.getId());
        if (existing == null) {
            items.put(p.getId(), new CartItem(p, quantityToAdd));
        } else {
            existing.setQuantity(existing.getQuantity() + quantityToAdd);
        }
    }

    /**
     * Nuevo método para actualizar la cantidad de un producto.
     * Si la cantidad es 0 o menos, se elimina el producto del carrito.
     */
    public void updateQuantity(Long productId, int quantity) {
        if (items.containsKey(productId)) {
            if (quantity <= 0) {
                remove(productId);
            } else {
                items.get(productId).setQuantity(quantity);
            }
        }
    }

    public void remove(Long productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Nuevo método para obtener el número total de artículos en el carrito.
     */
    public int getTotalItems() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public BigDecimal getSubtotal() {
        return getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getShipping() {
        // No hay costo de envío si el carrito está vacío
        return items.isEmpty() ? BigDecimal.ZERO : shipping;
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getShipping());
    }
}