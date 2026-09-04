package com.example.proyecto.controller;

import com.example.proyecto.model.rol;
import com.example.proyecto.model.permiso;
import com.example.proyecto.repository.RolRepository;
import com.example.proyecto.repository.PermisoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.proyecto.model.usuario;
import org.springframework.http.ResponseEntity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;

import java.util.*;

@Controller
@RequestMapping("/roles")
public class RolControlador {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    // ================= LISTAR =================
    @GetMapping
    public String listar(Model model) {

        // LISTAR ROLES
        List<rol> roles = rolRepository.findAll(
                Sort.by(Sort.Direction.DESC, "idRol")
        );

        model.addAttribute("roles", roles);


        // OBTENER USUARIO LOGUEADO

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof usuario) {

            usuario u = (usuario) auth.getPrincipal();

            // OBTENER PERMISOS DEL ROL
            List<String> permisos = u.getRol()
                    .getPermisos()
                    .stream()
                    .map(p -> p.getNombre())
                    .toList();

            // ENVIAR AL HTML
            model.addAttribute("permisos", permisos);
        }

        return "rol";
    }

    // ================= GUARDAR =================

    @PostMapping("/guardar")
    @ResponseBody
    public String guardar(String nombre) {

        rol r = new rol();
        r.setNombre(nombre);
        r.setEstado(1);

        rolRepository.save(r);
        return "ok";
    }

    // ================= ACTUALIZAR =================

    @PostMapping("/actualizar")
    @ResponseBody
    public String actualizar(Integer idRol, String nombre) {

        rol r = rolRepository.findById(idRol).orElseThrow();

        r.setNombre(nombre);
        rolRepository.save(r);

        return "ok";
    }

    // ================= ELIMINAR =================

    // ================= ELIMINAR =================

    @PostMapping("/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminar(@RequestParam Integer idRol) {
        try {
            // 🚨 VALIDACIÓN MAESTRA: Si hay usuarios activos vinculados, frenamos en seco
            long cantidadUsuarios = rolRepository.contarUsuariosActivosPorRol(idRol);

            if (cantidadUsuarios > 0) {
                return ResponseEntity.badRequest().body("RESTRICCION_USUARIOS");
            }

            rol r = rolRepository.findById(idRol).orElseThrow();

            r.setEstado(0);
            rolRepository.save(r);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }

    // ================= PERMISOS =================

    @GetMapping("/permisos/{idRol}")
    @ResponseBody
    public Map<String, Object> permisos(@PathVariable Integer idRol) {

        rol r = rolRepository.findById(idRol).orElseThrow();

        List<permiso> todos = permisoRepository.findByEstado(1);

        List<Integer> asignados = r.getPermisos()
                .stream()
                .map(permiso::getIdPermiso)
                .toList();

        Map<String, Object> res = new HashMap<>();
        res.put("todos", todos);
        res.put("asignados", asignados);

        return res;
    }

    // ================= GUARDAR PERMISOS =================

    @PostMapping("/permisos/guardar")
    @ResponseBody
    public String guardarPermisos(
            Integer idRol,
            @RequestParam(required = false) List<Integer> permisos
    ) {

        rol r = rolRepository.findById(idRol).orElseThrow();

        List<permiso> lista = (permisos == null)
                ? List.of()
                : permisoRepository.findAllById(permisos);

        r.setPermisos(lista);

        rolRepository.save(r);

        return "ok";
    }
    // ================= REACTIVAR ROL =================

    @PostMapping("/reactivar")
    @ResponseBody
    public String reactivar(Integer idRol) {

        rol r = rolRepository.findById(idRol).orElseThrow();

        r.setEstado(1);
        rolRepository.save(r);

        return "ok";
    }
}