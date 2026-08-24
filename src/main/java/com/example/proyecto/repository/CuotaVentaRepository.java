package com.example.proyecto.repository;

import com.example.proyecto.model.cuota_venta;
import com.example.proyecto.model.venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuotaVentaRepository extends JpaRepository<cuota_venta, Integer> {
    List<cuota_venta> findByVenta(venta venta);
}