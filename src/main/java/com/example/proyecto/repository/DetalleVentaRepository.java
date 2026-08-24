package com.example.proyecto.repository;

import com.example.proyecto.model.detalle_venta;
import com.example.proyecto.model.venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<detalle_venta, Integer> {
    // DECLARAR AQUÍ PARA SOLUCIONAR EL ERROR
    List<detalle_venta> findByVenta(venta venta);
}