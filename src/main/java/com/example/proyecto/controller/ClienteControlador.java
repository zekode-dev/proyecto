package com.example.proyecto.controller;

import com.example.proyecto.model.cliente;
import com.example.proyecto.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteControlador {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${decolecta.token}")
    private String token;

    // ==========================================================
    // ESCUCHA EL FETCH DE TU SPA Y SANA EL ERROR 404
    // ==========================================================
    @GetMapping
    public String listarClientes(Model model) {
        // Obtenemos todos los registros de la tabla cliente
        List<cliente> lista = clienteRepository.findAll();
        model.addAttribute("clientes", lista);
        return "clientes"; // Llama de manera exacta a tu archivo clientes.html
    }

    // ==========================================================
    // ACCIÓN: AGREGAR NUEVO CLIENTE (TEXTO PLANO PARA TU SPA)
    // ==========================================================
    @PostMapping("/guardar")
    @ResponseBody
    public String guardarCliente(@ModelAttribute cliente c) {
        try {
            Optional<cliente> existente = clienteRepository.findByDni(c.getDni());
            if (existente.isPresent()) {
                return "dni_existe";
            }

            c.setEstado(1); // Activo por defecto
            clienteRepository.save(c);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    // ==========================================================
    // ACCIÓN: ELIMINAR (Baja Lógica)
    // ==========================================================
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminarCliente(@PathVariable Integer id) {
        try {
            cliente c = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            c.setEstado(0); // Inactivo
            clienteRepository.save(c);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // ==========================================================
    // TU CONSULTA RENIEC ORIGINAL ACTUALIZADA A @CONTROLLER
    // ==========================================================
    @GetMapping("/buscar/{dni}")
    @ResponseBody
    public ResponseEntity<?> buscarCliente(@PathVariable String dni) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Optional<cliente> clienteOpt = clienteRepository.findByDni(dni);

            if (clienteOpt.isPresent()) {
                cliente c = clienteOpt.get();
                resp.put("origen", "BD");
                resp.put("existe", true);

                // Construcción exacta basada en los getters de tu modelo
                StringBuilder nombreCompleto = new StringBuilder(c.getNombres());
                if (c.getApellido_Paterno() != null && !c.getApellido_Paterno().isEmpty()) {
                    nombreCompleto.append(" ").append(c.getApellido_Paterno());
                }
                if (c.getApellido_Materno() != null && !c.getApellido_Materno().isEmpty()) {
                    nombreCompleto.append(" ").append(c.getApellido_Materno());
                }

                resp.put("nombres", nombreCompleto.toString().trim());
                return ResponseEntity.ok(resp);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = "https://api.decolecta.com/v1/reniec/dni?numero=" + dni;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<?, ?> body = response.getBody();

            resp.put("origen", "RENIEC");
            resp.put("existe", false);

            if (body != null) {
                resp.put("nombres", body.get("full_name"));
            }

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", "No se encontró información");
            return ResponseEntity.badRequest().body(resp);
        }
    }
    // ==========================================================
    // ACCIÓN: REACTIVAR CLIENTE
    // ==========================================================
    @PostMapping("/reactivar/{id}")
    @ResponseBody
    public String reactivarCliente(@PathVariable Integer id) {
        try {
            cliente c = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            c.setEstado(1); // Activo
            clienteRepository.save(c);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    // ==========================================================
    // ACCIÓN: MODIFICAR CLIENTE (CAMBIADO A MODELATTRIBUTE PARA RECIBIR FORMDATA)
    // ==========================================================
    @PostMapping("/modificar")
    @ResponseBody
    public String modificarCliente(@ModelAttribute cliente datosForm) {
        try {
            // Buscamos por el ID que viene directo del input hidden del formulario
            cliente c = clienteRepository.findById(datosForm.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // Solo actualizamos los campos permitidos que vienen del formulario
            c.setTelefono(datosForm.getTelefono());
            c.setCorreo(datosForm.getCorreo());
            c.setDireccion(datosForm.getDireccion());

            clienteRepository.save(c);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}