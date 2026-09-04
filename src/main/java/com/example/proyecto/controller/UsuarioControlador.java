package com.example.proyecto.controller;

import com.example.proyecto.model.usuario;
import com.example.proyecto.model.rol;
import com.example.proyecto.repository.UsuarioRepository;
import com.example.proyecto.repository.RolRepository;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =========================
    // LISTAR USUARIOS
    // =========================
    @GetMapping
    public String listarUsuarios(Model model) {

        List<usuario> usuarios = usuarioRepository.findAll(
                Sort.by(Sort.Direction.DESC, "idUsuario")
        );

        List<rol> roles = rolRepository.findByEstado(1);

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", roles);

        return "usuario";
    }


    // ========================= GUARDAR USUARIO ================================


    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarUsuario(
            @RequestParam String dni,
            @RequestParam String nombres,
            @RequestParam String usuario,
            @RequestParam(required = false) String correo,
            @RequestParam String password,
            @RequestParam(required = false) String telefono,
            @RequestParam Integer idRol
    ) {
        try {
            // === AQUÍ PONES LAS 3 LÍNEAS NUEVAS ===
            java.util.Optional<com.example.proyecto.model.usuario> existente = usuarioRepository.findByDni(dni);
            if (existente.isPresent()) { return ResponseEntity.ok("dni_existe"); }
            // ======================================

            rol rolEncontrado = rolRepository.findById(idRol)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            usuario u = new usuario();
            u.setDni(dni);
            u.setNombres(nombres);
            // ... (todo lo demás de tu código sigue exactamente igual abajo)
            u.setUsuario(usuario);
            u.setCorreo(correo);
            u.setTelefono(telefono);

            u.setPassword(passwordEncoder.encode(password));
            u.setEstado(1);
            u.setRol(rolEncontrado);

            usuarioRepository.save(u);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }


    // ========================= ACTUALIZAR USUARIO =========================


    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarUsuario(
            @RequestParam Integer idUsuario,
            @RequestParam String dni,
            @RequestParam String nombres,
            @RequestParam String usuario,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String password,
            @RequestParam Integer idRol
    ) {
        try {

            usuario existente = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            existente.setDni(dni);
            existente.setNombres(nombres);
            existente.setUsuario(usuario);
            existente.setCorreo(correo);
            existente.setTelefono(telefono);

            rol rolEncontrado = rolRepository.findById(idRol)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            existente.setRol(rolEncontrado);


            if (password != null && !password.isEmpty()) {
                existente.setPassword(passwordEncoder.encode(password));
            }

            usuarioRepository.save(existente);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }


    // ========================= ELIMINAR ==============================


    @PostMapping("/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminar(Integer idUsuario) {

        try {

            // NO ELIMINAR ADMIN PRINCIPAL
            if(idUsuario == 1){

                return ResponseEntity
                        .badRequest()
                        .body("admin_no_eliminar");
            }

            usuario u = usuarioRepository.findById(idUsuario)
                    .orElseThrow();

            u.setEstado(0);

            usuarioRepository.save(u);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("error");
        }
    }

    // ========================= REACTIVAR USUARIO ====================================


    @PostMapping("/reactivar")
    @ResponseBody
    public ResponseEntity<?> reactivarUsuario(@RequestParam Integer idUsuario) {

        try {

            usuario u = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            u.setEstado(1);

            usuarioRepository.save(u);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }
}