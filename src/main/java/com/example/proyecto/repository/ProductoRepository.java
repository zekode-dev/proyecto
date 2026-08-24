package com.example.proyecto.repository;

import com.example.proyecto.model.producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<producto, Integer> {

    List<producto> findByEstado(Integer estado);
    long countByEstado(Integer estado);
    @Query("""
        SELECT COUNT(DISTINCT d.producto)
        FROM producto_detalle d
        WHERE d.stock <= d.stockMinimo
    """)
    long contarStockBajo();

    @Query(value = "SELECT DISTINCT p.* FROM producto p " +
            "LEFT JOIN producto_detalle pd ON pd.id_producto = p.id_producto " +
            "WHERE p.estado = 1 " +
            "AND (:term IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :term, '%'))) " +
            "AND (:idCategoria IS NULL OR p.id_categoria = :idCategoria) " +
            "AND (:talla IS NULL OR pd.talla = :talla) " +
            "AND (:color IS NULL OR pd.color = :color) " +
            "AND (p.precio BETWEEN :precioMin AND :precioMax)",
            nativeQuery = true)
    List<producto> filtrarProductosWeb(
            @Param("term") String term,
            @Param("idCategoria") Integer idCategoria,
            @Param("talla") String talla,
            @Param("color") String color,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );

    // 🌟 NUEVO: Consulta nativa segura para agrupar stock total por categoría
    @Query(value = "SELECT c.nombre, SUM(pd.stock) FROM producto p " +
            "JOIN categoria c ON p.id_categoria = c.id_categoria " +
            "JOIN producto_detalle pd ON p.id_producto = pd.id_producto " +
            "WHERE p.estado = 1 " +
            "GROUP BY c.nombre", nativeQuery = true)
    List<Object[]> obtenerStockPorCategoria();
}