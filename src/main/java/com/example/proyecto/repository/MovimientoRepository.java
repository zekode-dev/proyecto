package com.example.proyecto.repository;

import com.example.proyecto.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<MovimientoInventario, Integer> {

    // Método clásico para ordenar todos los movimientos
    List<MovimientoInventario> findAllByOrderByFechaHoraDesc();

    // =========================================================================
    // SOLUCIÓN CON QUERY: Robusto, seguro y libre de errores por nombres de clases
    // =========================================================================
    @Query("SELECT m FROM MovimientoInventario m WHERE m.productoDetalle.producto.idProducto = :idProducto ORDER BY m.fechaHora DESC")
    List<MovimientoInventario> buscarHistorialPorProducto(@Param("idProducto") Integer idProducto);
}