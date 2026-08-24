package com.example.proyecto.repository;

import com.example.proyecto.model.categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<categoria, Integer> {

    List<categoria> findByEstado(Integer estado);

    // 🔥 NUEVA CONSULTA: Cuenta cuántos productos activos (estado 1) usan esta categoría
    // Nota: Reemplaza "producto" y "idCategoria" según los nombres exactos de tus entidades si varían.
    @Query("SELECT COUNT(p) FROM producto p WHERE p.categoria.idCategoria = :idCategoria AND p.estado = 1")
    long contarProductosActivosPorCategoria(@Param("idCategoria") Integer idCategoria);
}