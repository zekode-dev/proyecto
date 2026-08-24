package com.example.proyecto.repository;

import com.example.proyecto.model.permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermisoRepository
        extends JpaRepository<permiso, Integer> {

    List<permiso> findByEstado(Integer estado);

}