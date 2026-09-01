package com.example.proyecto.controller;

import com.example.proyecto.model.permiso;
import com.example.proyecto.repository.PermisoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
@RequestMapping("/permisos")
public class PermisoControlador {

    @Autowired
    private PermisoRepository permisoRepository;


    // ========================= LISTAR =================================

    @GetMapping
    public String listar(Model model) {

        List<permiso> permisos = permisoRepository.findAll(
                Sort.by(Sort.Direction.DESC, "idPermiso")
        );

        model.addAttribute("permisos", permisos);

        return "permiso";
    }


    // ========================= GUARDAR ===========================

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardar(@RequestParam String nombre) {

        try {

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Nombre vacío");
            }

            permiso p = new permiso();
            p.setNombre(nombre.trim().toUpperCase());
            p.setEstado(1);

            permisoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }


    // ========================= ACTUALIZAR ===========================

    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizar(
            @RequestParam Integer idPermiso,
            @RequestParam String nombre
    ) {

        try {

            permiso p = permisoRepository.findById(idPermiso)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

            p.setNombre(nombre.trim().toUpperCase());

            permisoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }


    // ========================= ELIMINAR  ==============================

    @PostMapping("/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminar(@RequestParam Integer idPermiso) {

        try {

            permiso p = permisoRepository.findById(idPermiso)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

            p.setEstado(0);

            permisoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }


    // ========================== REACTIVAR ===============================

    @PostMapping("/reactivar")
    @ResponseBody
    public ResponseEntity<?> reactivar(@RequestParam Integer idPermiso) {

        try {

            permiso p = permisoRepository.findById(idPermiso)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

            p.setEstado(1);

            permisoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }
}