package com.example.ecommerce.repository;

import com.example.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// La clave primaria ahora es String, no Long
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    // El método findByEmail ya no es necesario, porque el email (correo)
    // es la clave primaria. Podemos usar el método findById() que ya viene incluido.
}