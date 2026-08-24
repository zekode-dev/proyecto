package com.example.proyecto.seguridad;

import com.example.proyecto.model.usuario;
import com.example.proyecto.model.permiso;
import com.example.proyecto.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        usuario user = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                user.getUsuario(),
                user.getPassword(),
                user.getEstado() == 1,
                true,
                true,
                true,
                List.of()
        );
    }
}