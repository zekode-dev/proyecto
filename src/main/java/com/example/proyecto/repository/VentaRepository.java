package com.example.proyecto.repository;

import com.example.proyecto.model.venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VentaRepository extends JpaRepository<venta, Integer> {

    List<venta> findByEstado(Integer estado, Sort sort);

    // 🌟 NUEVO: Consulta nativa para agrupar ventas mensuales por orden cronológico
    @Query(value = "SELECT DATE_FORMAT(v.fecha, '%b') AS mes, SUM(v.total) FROM venta v " +
            "WHERE v.estado = 1 " +
            "GROUP BY DATE_FORMAT(v.fecha, '%b'), MONTH(v.fecha) " +
            "ORDER BY MONTH(v.fecha) ASC", nativeQuery = true)
    List<Object[]> obtenerVentasMensualesHistorial();
}