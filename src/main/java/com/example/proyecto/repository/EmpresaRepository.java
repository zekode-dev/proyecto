package com.example.proyecto.repository;

import com.example.proyecto.model.empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<empresa, Integer> {
}