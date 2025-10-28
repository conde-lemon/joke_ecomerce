package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class CartService {
    private final Cart cart = new Cart();

    public Cart getCart() {
        return cart;
    }

    public void add(Product p, int qty) {
        cart.add(p, qty);
    }

    public void remove(Long productId) {
        cart.remove(productId);
    }

    public void clear() {
        cart.clear();
    }
}
