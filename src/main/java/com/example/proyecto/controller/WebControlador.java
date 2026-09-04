package com.example.proyecto.controller;

import com.example.proyecto.model.empresa;
import com.example.proyecto.model.logo;
import com.example.proyecto.model.producto;
import com.example.proyecto.model.cliente;
import com.example.proyecto.repository.EmpresaRepository;
import com.example.proyecto.repository.LogoRepository;
import com.example.proyecto.repository.ProductoRepository;
import com.example.proyecto.repository.SliderRepository;
import com.example.proyecto.repository.DetalleProductoRepository;
import com.example.proyecto.repository.CategoriaRepository;
import com.example.proyecto.repository.ClienteRepository;
import com.example.proyecto.service.ReniecService; // <-- Importación correcta de tu servicio

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class WebControlador {

    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private DetalleProductoRepository detalleProductoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ClienteRepository clienteRepository; // <-- Inyección del repositorio de clientes

    @Autowired
    private ReniecService reniecService; // <-- ESTO FALTABA: Inyección de tu servicio de Reniec

    @GetMapping("/web")
    public String web(Model model){

        // =====================================
        // SLIDERS
        // =====================================
        model.addAttribute("sliders", sliderRepository.findByEstado(1));

        // =====================================
        // PRODUCTOS
        // =====================================
        model.addAttribute("productos", productoRepository.findByEstado(1));

        // =====================================
        // LOGO ACTIVO
        // =====================================
        logo logoActivo = logoRepository.findFirstByEstado(1);
        model.addAttribute("logoActivo", logoActivo);

        // =====================================
        // EMPRESA
        // =====================================
        empresa empresaData;
        if(empresaRepository.count() > 0){
            empresaData = empresaRepository.findAll().get(0);
        } else {
            empresaData = new empresa();
        }
        model.addAttribute("empresa", empresaData);

        // =====================================================================
        // DATA PARA FILTROS DINÁMICOS DE LA WEB
        // =====================================================================
        model.addAttribute("categoriasDisponibles", categoriaRepository.findByEstado(1));
        model.addAttribute("tallasDisponibles", detalleProductoRepository.findDistinctTallas());
        model.addAttribute("coloresDisponibles", detalleProductoRepository.findDistinctColores());

        return "web";
    }

    // =====================================================================
    // VISTA DETALLE DE PRODUCTO
    // =====================================================================
    @GetMapping("/web/producto/{id}")
    public String verDetalleProducto(@PathVariable("id") Integer id, Model model) {
        producto prod = productoRepository.findById(id).orElse(null);

        if (prod == null || prod.getEstado() != 1) {
            return "redirect:/web";
        }

        model.addAttribute("p", prod);
        model.addAttribute("logoActivo", logoRepository.findFirstByEstado(1));

        return "detalle";
    }

    // =====================================================================
    // VISTA DE LA BOLSA DE COMPRAS (CARRITO)
    // =====================================================================
    @GetMapping("/web/carrito")
    public String verCarrito() {
        return "redirect:/web";
    }

    // =====================================================================
    // ENDPOINT ASÍNCRONO PARA LA WEB PÚBLICA (CORREGIDO)
    // =====================================================================
    @GetMapping("/web/clientes/buscar/{dni}")
    @ResponseBody
    public ResponseEntity<?> buscarClientePorDniWebPublico(@PathVariable("dni") String dni) {
        try {
            Map<String, Object> response = new HashMap<>();

            // 1. Buscamos usando el objeto inyectado 'clienteRepository' (con minúscula)
            Optional<cliente> cliOpt = clienteRepository.findByDni(dni);

            if (cliOpt.isPresent()) {
                cliente c = cliOpt.get();
                response.put("origen", "BD");
                response.put("nombres", c.getNombres());
                response.put("apellido_Paterno", c.getApellido_Paterno());
                response.put("apellido_Materno", c.getApellido_Materno());
                return ResponseEntity.ok(response);
            }

            // 2. Si no está en BD, consultamos tu ReniecService ya inyectado arriba
            Map<?, ?> datosReniec = reniecService.consultarDni(dni);
            if (datosReniec != null && datosReniec.get("first_name") != null) {
                response.put("origen", "RENIEC");
                response.put("nombres", (String) datosReniec.get("first_name"));
                response.put("apellido_Paterno", (String) datosReniec.get("first_last_name"));
                response.put("apellido_Materno", (String) datosReniec.get("second_last_name"));
                return ResponseEntity.ok(response);
            }

            response.put("error", "No se encontraron datos para el DNI ingresado.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResp = new HashMap<>();
            errorResp.put("error", "Error al procesar la consulta: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResp);
        }
    }
}