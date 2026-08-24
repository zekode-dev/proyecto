package com.example.proyecto.repository;

import com.example.proyecto.model.slider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SliderRepository extends JpaRepository<slider, Integer> {

    List<slider> findByEstado(Integer estado);

}