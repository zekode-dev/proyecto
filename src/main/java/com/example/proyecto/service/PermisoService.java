package com.example.proyecto.service;

import com.example.proyecto.model.usuario;
import com.example.proyecto.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermisoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // =========================
    // VALIDAR PERMISO
    // =========================

    public boolean tienePermiso(String username,
                                String nombrePermiso) {

        usuario u = usuarioRepository
                .findByUsuario(username)
                .orElse(null);

        // SI NO EXISTE USUARIO
        if (u == null) {
            return false;
        }

        // SI NO TIENE ROL
        if (u.getRol() == null) {
            return false;
        }

        // VALIDAR PERMISOS
        return u.getRol()
                .getPermisos()
                .stream()
                .anyMatch(p ->

                        // NOMBRE DEL PERMISO
                        p.getNombre().equals(nombrePermiso)

                                // SOLO ACTIVOS
                                && p.getEstado() == 1
                );
    }
}