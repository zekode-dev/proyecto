package com.example.proyecto.controller;

import com.example.proyecto.model.producto;
import com.example.proyecto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/api/web/productos/filtrar")
    public List<producto> filtrar(
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
            @RequestParam(value = "talla", required = false) String talla,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "rangoPrecio", required = false) String rangoPrecio) {

        // Limpieza de textos vacíos
        String buscarTerm = (term == null || term.trim().isEmpty()) ? null : term.trim();
        String buscarTalla = (talla == null || talla.trim().isEmpty()) ? null : talla.trim();
        String buscarColor = (color == null || color.trim().isEmpty()) ? null : color.trim();

        // Ajuste clave: Usar BigDecimal para hacer match con tu modelo de Producto
        BigDecimal precioMin = BigDecimal.ZERO;
        BigDecimal precioMax = new BigDecimal("999999.00");

        if (rangoPrecio != null && rangoPrecio.contains("-")) {
            try {
                String[] partes = rangoPrecio.split("-");
                precioMin = new BigDecimal(partes[0]);
                precioMax = new BigDecimal(partes[1]);
            } catch (NumberFormatException e) {
                // Si falla el parseo, se mantiene el rango por defecto
            }
        }

        return productoRepository.filtrarProductosWeb(
                buscarTerm,
                idCategoria,
                buscarTalla,
                buscarColor,
                precioMin,
                precioMax
        );
    }
}