package com.example.proyecto.repository;

import com.example.proyecto.model.logo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogoRepository
        extends JpaRepository<logo,Integer> {

    logo findFirstByEstado(Integer estado);

}