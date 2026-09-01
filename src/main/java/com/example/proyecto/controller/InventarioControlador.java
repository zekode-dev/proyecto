package com.example.proyecto.controller;

import com.example.proyecto.model.MovimientoInventario;
import com.example.proyecto.repository.MovimientoRepository;
import com.example.proyecto.repository.DetalleProductoRepository;
import com.example.proyecto.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioControlador {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private DetalleProductoRepository detalleRepository;

    @Autowired
    private InventarioService inventarioService;

    // Listar todo el historial de movimientos (Para la vista general si la usas)
    @GetMapping
    public String listarMovimientos(Model model) {
        model.addAttribute("movimientos", movimientoRepository.findAllByOrderByFechaHoraDesc());
        model.addAttribute("variantes", detalleRepository.findAll());
        return "inventario";
    }

    // Procesar el ajuste de inventario modificado con Observación
    @PostMapping("/guardar")
    @ResponseBody
    public String guardarMovimiento(
            @RequestParam Integer idDetalle,
            @RequestParam Integer cantidad,
            @RequestParam String tipoMovimiento,
            @RequestParam String motivo,
            @RequestParam(required = false) String observacion, // 🌟 Nuevo parámetro
            Principal principal) {

        try {
            String username = (principal != null) ? principal.getName() : "Sistema_Anonimo";

            // NOTA: Asegúrate de añadir el parámetro 'observacion' en las firmas de
            // inventarioService.registrarEntrada e inventarioService.registrarSalida dentro de tu Service.
            if (tipoMovimiento.equals("ENTRADA")) {
                inventarioService.registrarEntrada(idDetalle, cantidad, motivo, username, observacion);
            } else {
                inventarioService.registrarSalida(idDetalle, cantidad, motivo, username, observacion);
            }
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    // =========================================================================
    // NUEVO MÉTODO ASÍNCRONO: Retorna el historial de un producto en formato JSON
    // =========================================================================
    // =========================================================================
    // NUEVO MÉTODO ASÍNCRONO: Retorna el historial de un producto en formato JSON
    // =========================================================================
    @GetMapping("/producto/{idProducto}")
    @ResponseBody
    public List<MovimientoInventario> obtenerHistorialPorProducto(@PathVariable Integer idProducto) {
        // Llamamos al nuevo método con la Query integrada
        return movimientoRepository.buscarHistorialPorProducto(idProducto);
    }
}