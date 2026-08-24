package com.example.proyecto.repository;

import com.example.proyecto.model.producto;
import com.example.proyecto.model.producto_detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleProductoRepository
        extends JpaRepository<producto_detalle, Integer> {

    List<producto_detalle> findByProducto(producto producto);

    List<producto_detalle> findByProductoAndEstado(
            producto producto,
            Integer estado
    );

    // NUEVO: Jalar las tallas únicas reales de la BD
    @Query("SELECT DISTINCT pd.talla FROM producto_detalle pd WHERE pd.talla IS NOT NULL ORDER BY pd.talla ASC")
    List<String> findDistinctTallas();

    // NUEVO: Jalar los colores únicos reales de la BD
    @Query("SELECT DISTINCT pd.color FROM producto_detalle pd WHERE pd.color IS NOT NULL ORDER BY pd.color ASC")
    List<String> findDistinctColores();
}