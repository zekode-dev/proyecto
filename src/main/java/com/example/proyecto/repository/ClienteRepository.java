package com.example.proyecto.repository;

import com.example.proyecto.model.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository
        extends JpaRepository<cliente,Integer> {

    Optional<cliente> findByDni(String dni);
    long countByEstado(Integer estado);
}