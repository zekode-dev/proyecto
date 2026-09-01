package com.example.proyecto.controller;

import com.example.proyecto.model.producto;
import com.example.proyecto.repository.ClienteRepository;
import com.example.proyecto.repository.ProductoRepository;
import com.example.proyecto.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // 🌟 1. MÉTRICAS DE LAS TARJETAS SUPERIORES
        long totalProductos = productoRepository.countByEstado(1);
        long totalClientes = clienteRepository.countByEstado(1);
        long stockBajo = productoRepository.contarStockBajo();

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("stockBajo", stockBajo);

        // Calcular ingresos totales de ventas activas
        double ingresosTotales = ventaRepository.findAll().stream()
                .filter(v -> v != null && v.getEstado() == 1)
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal().doubleValue() : 0.0)
                .sum();
        model.addAttribute("totalVentas", ingresosTotales);

        // 🌟 2. DATA PARA LAS NUEVAS TABLAS EN VIVO (Pasadas directo a Thymeleaf)
        List<Object[]> datosVentas = ventaRepository.obtenerVentasMensualesHistorial();
        List<Object[]> datosCategorias = productoRepository.obtenerStockPorCategoria();

        model.addAttribute("datosVentas", datosVentas);
        model.addAttribute("datosCategorias", datosCategorias);

        // 🌟 3. MONITOR DE ALERTA INFERIOR
        List<producto> productosCriticos = productoRepository.findByEstado(1).stream()
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("productosCriticos", productosCriticos);

        return "dashboard";
    }
}