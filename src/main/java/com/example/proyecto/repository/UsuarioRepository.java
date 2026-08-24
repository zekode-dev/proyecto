package com.example.proyecto.repository;

import com.example.proyecto.model.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<usuario, Integer> {

    Optional<usuario> findByUsuario(String usuario);

    Optional<usuario> findByDni(String dni);

    // MÉTODO NUEVO PARA BUSCAR AL USUARIO POR EMAIL EN LA BASE DE DATOS
    Optional<usuario> findByCorreo(String correo);
}