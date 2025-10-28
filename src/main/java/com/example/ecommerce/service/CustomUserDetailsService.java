package com.example.ecommerce.service;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + email));

        // Convierte la cadena de roles (ej: "USER,ADMIN") en una lista de autoridades para Spring Security
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(usuario.getRoles().split(","))
                        .map(role -> "ROLE_" + role) // Spring Security necesita el prefijo ROLE_
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new User(usuario.getCorreo(), usuario.getContrasena(), authorities);
    }
}