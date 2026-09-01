package com.example.proyecto.controller;

import com.example.proyecto.repository.ProductoRepository;
import com.example.proyecto.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    // 🌟 1. GRÁFICO DE TORTA DE STOCK REAL
    @GetMapping("/grafico-categorias")
    public Map<String, Object> getDatosCategorias() {
        Map<String, Object> respuesta = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Long> valores = new ArrayList<>();

        // 🌟 Línea corregida dentro de DashboardApiController:
        List<Object[]> datosBBDD = productoRepository.obtenerStockPorCategoria();

        for (Object[] fila : datosBBDD) {
            String categoria = fila[0] != null ? fila[0].toString() : "Sin Categoría";
            Long cantidad = fila[1] != null ? ((Number) fila[1]).longValue() : 0L;
            labels.add(categoria);
            valores.add(cantidad);
        }

        if (labels.isEmpty()) {
            labels = List.of("Sin Stock");
            valores = List.of(0L);
        }

        respuesta.put("labels", labels);
        respuesta.put("valores", valores);
        return respuesta;
    }

    // 🌟 2. GRÁFICO HISTÓRICO DE VENTAS REAL
    @GetMapping("/grafico-ventas")
    public Map<String, Object> getDatosVentas() {
        Map<String, Object> respuesta = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> valores = new ArrayList<>();

        List<Object[]> datosVentas = ventaRepository.obtenerVentasMensualesHistorial();

        for (Object[] fila : datosVentas) {
            String mes = fila[0] != null ? fila[0].toString() : "Mes";
            Double monto = fila[1] != null ? ((Number) fila[1]).doubleValue() : 0.0;
            labels.add(mes);
            valores.add(monto);
        }

        if (labels.isEmpty()) {
            labels = List.of("Sin Ventas");
            valores = List.of(0.0);
        }

        respuesta.put("labels", labels);
        respuesta.put("valores", valores);
        return respuesta;
    }
}