package com.example.proyecto.service;

import com.example.proyecto.model.MovimientoInventario;
import com.example.proyecto.model.producto_detalle;
import com.example.proyecto.repository.MovimientoRepository;
import com.example.proyecto.repository.DetalleProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class InventarioService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private DetalleProductoRepository detalleRepository; // 🌟 Usaremos este nombre exacto

    // REGISTRAR UNA ENTRADA DE STOCK (Suma al inventario)
    @Transactional
    public void registrarEntrada(Integer idDetalle, Integer cantidad, String motivo, String username, String observacion) {
        // CORREGIDO: Se cambió detalleProductoRepository por detalleRepository
        producto_detalle detalle = detalleRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));

        // Sumar stock real
        detalle.setStock(detalle.getStock() + cantidad);

        // CORREGIDO: Se cambió la clase con mayúscula por la variable inyectada detalleRepository
        detalleRepository.save(detalle);

        // Guardar el registro en el Kardex de auditoría
        MovimientoInventario mov = new MovimientoInventario();
        mov.setProductoDetalle(detalle);
        mov.setCantidad(cantidad);
        mov.setTipoMovimiento("ENTRADA");
        mov.setMotivo(motivo);
        mov.setUsuario(username);
        mov.setFechaHora(LocalDateTime.now());
        mov.setObservacion(observacion);

        movimientoRepository.save(mov);
    }

    // REGISTRAR UNA SALIDA DE STOCK (Resta al inventario)
    @Transactional
    public void registrarSalida(Integer idDetalle, Integer cantidad, String motivo, String username, String observacion) {
        // CORREGIDO: Se cambió detalleProductoRepository por detalleRepository
        producto_detalle detalle = detalleRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));

        if (detalle.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para realizar la salida.");
        }

        // Restar stock real
        detalle.setStock(detalle.getStock() - cantidad);

        // CORREGIDO: Se unificó a detalleRepository
        detalleRepository.save(detalle);

        // Guardar el registro en el Kardex de auditoría
        MovimientoInventario mov = new MovimientoInventario();
        mov.setProductoDetalle(detalle);
        mov.setCantidad(cantidad);
        mov.setTipoMovimiento("SALIDA");
        mov.setMotivo(motivo);
        mov.setUsuario(username);
        mov.setFechaHora(LocalDateTime.now());
        mov.setObservacion(observacion);

        movimientoRepository.save(mov);
    }
}