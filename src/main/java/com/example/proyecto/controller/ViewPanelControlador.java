package com.example.proyecto.controller;

import com.example.proyecto.service.PermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ViewPanelControlador {

    @Autowired
    private PermisoService permisoService;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "index";
    }

    // CARGA EL MARCO GENERAL (Sidebar y menús)
    @GetMapping("/panel")
    public String mostrarPanel(Model model, Principal principal) {
        String username = principal.getName();

        model.addAttribute("verDashboard", permisoService.tienePermiso(username, "VER_DASHBOARD"));
        model.addAttribute("verUsuarios", permisoService.tienePermiso(username, "VER_USUARIOS"));
        model.addAttribute("verRoles", permisoService.tienePermiso(username, "VER_ROLES"));
        model.addAttribute("verPermisos", permisoService.tienePermiso(username, "VER_PERMISOS"));
        model.addAttribute("verProductos", permisoService.tienePermiso(username, "VER_PRODUCTOS"));
        model.addAttribute("verCategorias", permisoService.tienePermiso(username, "VER_CATEGORIAS"));
        model.addAttribute("verVentas", permisoService.tienePermiso(username, "VER_VENTAS"));
        model.addAttribute("verClientes", permisoService.tienePermiso(username, "VER_CLIENTES"));
        model.addAttribute("verConfiguracion", permisoService.tienePermiso(username, "VER_CONFIGURACION"));

        return "panel";
    }
}