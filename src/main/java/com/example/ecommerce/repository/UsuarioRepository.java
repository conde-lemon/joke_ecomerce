package com.example.ecommerce.repository;

import com.example.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// La clave primaria ahora es String (correo), no Long
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    /**
     * Busca un usuario por su nombre de usuario único.
     */
    Optional<Usuario> findByUsuario(String usuario);

    /**
     * Busca un usuario por su correo electrónico (clave primaria).
     */
    Optional<Usuario> findByCorreo(String correo);
}