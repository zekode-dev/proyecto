package com.example.proyecto.controller;

import com.example.proyecto.model.categoria;

import com.example.proyecto.model.rol;
import com.example.proyecto.repository.CategoriaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.proyecto.model.usuario;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;

import java.util.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaControlador {

    @Autowired
    private CategoriaRepository categoriaRepository;


    // ================= LISTAR =================
    @GetMapping
    public String listarCategoria(Model model) {

        List<categoria> categorias = categoriaRepository.findAll(
                Sort.by(Sort.Direction.DESC, "idCategoria")
        );

        model.addAttribute("categorias", categorias);

        return "categoria";
    }

    // ================= GUARDAR =================

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCategoria(
            @RequestParam String descripcion,
            @RequestParam String nombre
    ) {
        try {

            categoria c = new categoria();
            c.setDescripcion(descripcion);
            c.setNombre(nombre);
            c.setEstado(1);

            categoriaRepository.save(c);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }

    // ================= ACTUALIZAR =================

    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarCategoria(
            @RequestParam Integer idCategoria,
            @RequestParam String descripcion,
            @RequestParam String nombre

    ) {
        try {

            categoria existente = categoriaRepository.findById(idCategoria)
                    .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

            existente.setDescripcion(descripcion);
            existente.setNombre(nombre);

            categoriaRepository.save(existente);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }

    // ================= ELIMINAR =================

    // ================= ELIMINAR =================

    @PostMapping("/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminarCategoria(@RequestParam Integer idCategoria) {
        try {
            // 🚨 VALIDACIÓN MAESTRA: Validamos si tiene productos asignados activos
            long cantidadProductos = categoriaRepository.contarProductosActivosPorCategoria(idCategoria);

            if (cantidadProductos > 0) {
                return ResponseEntity.badRequest().body("RESTRICCION_PRODUCTOS");
            }

            categoria c = categoriaRepository.findById(idCategoria)
                    .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

            c.setEstado(0);
            categoriaRepository.save(c);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }
    // ================= REACTIVAR ROL =================

    @PostMapping("/reactivar")
    @ResponseBody
    public String reactivar(Integer idCategoria) {

        categoria c = categoriaRepository.findById(idCategoria).orElseThrow();

        c.setEstado(1);
        categoriaRepository.save(c);

        return "ok";
    }
}