package com.example.proyecto.controller;

import com.example.proyecto.dto.VentaDetalleDTO;
import com.example.proyecto.dto.VentaRequestDTO;
import com.example.proyecto.model.*;
import com.example.proyecto.repository.*;
import com.example.proyecto.service.ReniecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/ventas")
public class VentaControlador {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleProductoRepository detalleProductoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private CuotaVentaRepository cuotaVentaRepository;

    @Autowired
    private ReniecService reniecService;

    // =========================================================================
    // 1. VISTAS Y RENDERIZADO (HTML)
    // =========================================================================

    @GetMapping
    public String listarVentas(Model model) {
        List<venta> ventas = ventaRepository.findAll(Sort.by(Sort.Direction.DESC, "idVenta"));

        Map<Integer, Boolean> alertasVencimiento = new HashMap<>();

        for (venta v : ventas) {
            if (v.getEstado() == 2) {
                List<cuota_venta> cuotas = cuotaVentaRepository.findByVenta(v);
                boolean tieneRiesgo = cuotas.stream()
                        .anyMatch(c -> "PENDIENTE".equals(c.getEstado()) &&
                                (c.getFechaVencimiento().isBefore(LocalDate.now().plusDays(3))));
                alertasVencimiento.put(v.getIdVenta(), tieneRiesgo);
            } else {
                alertasVencimiento.put(v.getIdVenta(), false);
            }
        }

        model.addAttribute("ventas", ventas);
        model.addAttribute("alertasVencimiento", alertasVencimiento);
        model.addAttribute("productos", productoRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre")));

        return "ventas";
    }

    @GetMapping("/nuevo")
    public String nuevaVenta(Model model) {
        List<producto> productos = productoRepository.findAll(
                Sort.by(Sort.Direction.ASC, "nombre")
        );
        model.addAttribute("productos", productos);
        return "nueva_venta";
    }

    // =========================================================================
    // 2. PROCESAMIENTO TRANSACCIONAL (GUARDAR VENTA)
    // =========================================================================

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarVenta(@RequestBody VentaRequestDTO request) {
        try {
            cliente cli = clienteRepository.findByDni(request.getDni()).orElse(null);

            if (cli == null) {
                cli = new cliente();
                cli.setDni(request.getDni());
                cli.setEstado(1);

                try {
                    Map<?, ?> datosReniec = reniecService.consultarDni(request.getDni());

                    if (datosReniec != null && datosReniec.get("first_name") != null) {
                        cli.setNombres((String) datosReniec.get("first_name"));
                        cli.setApellido_Paterno((String) datosReniec.get("first_last_name"));
                        cli.setApellido_Materno((String) datosReniec.get("second_last_name"));
                    } else {
                        cli.setNombres(request.getNombreCliente());
                        cli.setApellido_Paterno(request.getApellidoPaterno());
                        cli.setApellido_Materno(request.getApellidoMaterno());
                    }
                } catch (Exception e) {
                    cli.setNombres(request.getNombreCliente());
                    cli.setApellido_Paterno(request.getApellidoPaterno());
                    cli.setApellido_Materno(request.getApellidoMaterno());
                }
                clienteRepository.save(cli);
            }

            venta venta = new venta();
            venta.setCliente(cli);
            venta.setSerie("B001");
            venta.setNumero(String.valueOf(System.currentTimeMillis()));
            venta.setSubtotal(request.getSubtotal());
            venta.setIgv(request.getIgv());
            venta.setTotal(request.getTotal());
            venta.setMetodoPago(request.getMetodoPago());
            venta.setTipoPago(request.getTipoPago());
            venta.setCodigoOperacion(request.getCodigoOperacion());
            venta.setCuotas(request.getCuotas());
            venta.setMontoCuota(request.getMontoCuota());

            // 1. Averiguamos si es una venta física de tienda o una venta online
            boolean esVentaWeb = false;
            if ("YAPE".equals(request.getMetodoPago()) || "PLIN".equals(request.getMetodoPago())) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                    esVentaWeb = true;
                }
            }

            // 2. Asignación del estado correspondiente
            if ("CREDITO".equals(request.getTipoPago())) {
                venta.setEstado(2);
            } else if (esVentaWeb) {
                venta.setEstado(3); // Estado 'Por Verificar' (Por Internet)
            } else {
                venta.setEstado(1); // Venta Regular en Tienda Física
            }

            ventaRepository.save(venta);

            for (VentaDetalleDTO item : request.getDetalles()) {
                producto_detalle detalleProducto = detalleProductoRepository
                        .findById(item.getIdDetalle())
                        .orElseThrow(() -> new RuntimeException("Variación de producto no encontrada"));

                // Validación preventiva de Stock en ambos casos
                if (item.getCantidad() > detalleProducto.getStock()) {
                    throw new RuntimeException("Stock insuficiente para: " +
                            detalleProducto.getProducto().getNombre());
                }

                // 🌟 NUEVA REGLA DEL DOCENTE: Si es venta web (Estado 3), NO restamos stock del inventario aún
                if (!esVentaWeb) {
                    detalleProducto.setStock(detalleProducto.getStock() - item.getCantidad());
                    detalleProductoRepository.save(detalleProducto);
                }

                detalle_venta detalleVenta = new detalle_venta();
                detalleVenta.setVenta(venta);
                detalleVenta.setProductoDetalle(detalleProducto);
                detalleVenta.setCantidad(item.getCantidad());
                detalleVenta.setPrecio(item.getPrecio());
                detalleVenta.setSubtotal(item.getSubtotal());
                detalleVentaRepository.save(detalleVenta);
            }

            if ("CREDITO".equals(request.getTipoPago())) {
                LocalDate fechaBase = LocalDate.parse(request.getFechaPrimerVencimiento());

                for (int i = 1; i <= request.getCuotas(); i++) {
                    cuota_venta cuota = new cuota_venta();
                    cuota.setVenta(venta);
                    cuota.setNroCuota(i);
                    cuota.setMonto(request.getMontoCuota());
                    cuota.setFechaVencimiento(fechaBase.plusMonths(i - 1));
                    cuota.setEstado("PENDIENTE");
                    cuotaVentaRepository.save(cuota);
                }
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("idVenta", venta.getIdVenta());
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // =========================================================================
    // 3. COMPONENTES ASÍNCRONOS (APIS PARA MODALES Y ACCIONES)
    // =========================================================================

    @GetMapping("/detalles-producto/{idProducto}")
    @ResponseBody
    public List<producto_detalle> obtenerDetallesProducto(@PathVariable Integer idProducto) {
        producto p = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return detalleProductoRepository.findByProductoAndEstado(p, 1);
    }

    @GetMapping("/buscar/{idVenta}")
    @ResponseBody
    public ResponseEntity<?> obtenerVentaPorId(@PathVariable Integer idVenta) {
        Optional<venta> vOpt = ventaRepository.findById(idVenta);
        if (vOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        venta v = vOpt.get();
        List<detalle_venta> detalles = detalleVentaRepository.findByVenta(v);

        Map<String, Object> response = new HashMap<>();
        response.put("idVenta", v.getIdVenta());
        response.put("serie", v.getSerie());
        response.put("numero", v.getNumero());

        StringBuilder nombreCompleto = new StringBuilder(v.getCliente().getNombres());
        if (v.getCliente().getApellido_Paterno() != null) {
            nombreCompleto.append(" ").append(v.getCliente().getApellido_Paterno());
        }
        if (v.getCliente().getApellido_Materno() != null) {
            nombreCompleto.append(" ").append(v.getCliente().getApellido_Materno());
        }

        response.put("clienteNombre", nombreCompleto.toString().trim());
        response.put("clienteDni", v.getCliente().getDni());
        response.put("tipoPago", v.getTipoPago());
        response.put("metodoPago", v.getMetodoPago());
        response.put("codigoOperacion", v.getCodigoOperacion());
        response.put("subtotal", v.getSubtotal());
        response.put("igv", v.getIgv());
        response.put("total", v.getTotal());
        response.put("cuotas", v.getCuotas());
        response.put("montoCuota", v.getMontoCuota());

        List<Map<String, Object>> detallesJson = detalles.stream().map(d -> {
            Map<String, Object> det = new HashMap<>();
            det.put("cantidad", d.getCantidad());
            det.put("precio", d.getPrecio());
            det.put("subtotal", d.getSubtotal());
            det.put("producto", d.getProductoDetalle().getProducto().getNombre());
            det.put("talla", d.getProductoDetalle().getTalla());
            return det;
        }).toList();

        response.put("detalles", detallesJson);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cuotas/{idVenta}")
    @ResponseBody
    public ResponseEntity<?> obtenerCuotasDeVenta(@PathVariable Integer idVenta) {
        venta v = ventaRepository.findById(idVenta).orElse(null);
        if (v == null) return ResponseEntity.notFound().build();

        List<cuota_venta> cuotas = cuotaVentaRepository.findByVenta(v);
        List<Map<String, Object>> cuotasJson = cuotas.stream().map(c -> {
            Map<String, Object> cuo = new HashMap<>();
            cuo.put("idCuota", c.getIdCuota());
            cuo.put("nroCuota", c.getNroCuota());
            cuo.put("monto", c.getMonto());
            cuo.put("fechaVencimiento", c.getFechaVencimiento().toString());
            cuo.put("fechaPago", c.getFechaPago() != null ? c.getFechaPago().toString() : null);
            cuo.put("estado", c.getEstado());
            cuo.put("metodoPago", c.getMetodoPago());
            cuo.put("codigoOperacion", c.getCodigoOperacion());
            return cuo;
        }).toList();

        Map<String, Object> response = new HashMap<>();

        StringBuilder nombreCompleto = new StringBuilder(v.getCliente().getNombres());
        if (v.getCliente().getApellido_Paterno() != null) {
            nombreCompleto.append(" ").append(v.getCliente().getApellido_Paterno());
        }
        if (v.getCliente().getApellido_Materno() != null) {
            nombreCompleto.append(" ").append(v.getCliente().getApellido_Materno());
        }

        response.put("clienteNombre", nombreCompleto.toString().trim());
        response.put("clienteDni", v.getCliente().getDni());
        response.put("cuotas", cuotasJson);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/eliminar/{idVenta}")
    @ResponseBody
    public ResponseEntity<?> eliminarVentaLogica(@PathVariable Integer idVenta) {
        try {
            venta v = ventaRepository.findById(idVenta)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            v.setEstado(0);
            ventaRepository.save(v);

            List<detalle_venta> detalles = detalleVentaRepository.findByVenta(v);
            for (detalle_venta det : detalles) {
                producto_detalle prodDet = det.getProductoDetalle();
                prodDet.setStock(prodDet.getStock() + det.getCantidad());
                detalleProductoRepository.save(prodDet);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/cuotas/pagar")
    @ResponseBody
    public ResponseEntity<?> pagarCuota(@RequestBody Map<String, Object> datos) {
        try {
            Integer idCuota = (Integer) datos.get("idCuota");
            String metodoPago = (String) datos.get("metodoPago");
            String codigoOperacion = (String) datos.get("codigoOperacion");

            cuota_venta cuota = cuotaVentaRepository.findById(idCuota)
                    .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

            if ("PAGADO".equals(cuota.getEstado())) {
                return ResponseEntity.badRequest().body("Esta cuota ya fue pagada.");
            }

            cuota.setEstado("PAGADO");
            cuota.setFechaPago(LocalDate.now());
            cuota.setMetodoPago(metodoPago);
            cuota.setCodigoOperacion(codigoOperacion);
            cuotaVentaRepository.save(cuota);

            venta v = cuota.getVenta();
            List<cuota_venta> todasLasCuotas = cuotaVentaRepository.findByVenta(v);

            long cuotasPendientes = todasLasCuotas.stream()
                    .filter(c -> "PENDIENTE".equals(c.getEstado()))
                    .count();

            if (!todasLasCuotas.isEmpty() && cuotasPendientes == 0) {
                v.setEstado(1);
                ventaRepository.save(v);
            } else {
                v.setEstado(2);
                ventaRepository.save(v);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Cuota N° " + cuota.getNroCuota() + " pagada con éxito.");

            Map<String, Object> ticket = new HashMap<>();
            ticket.put("idCuota", cuota.getIdCuota());
            ticket.put("nroCuota", cuota.getNroCuota());
            ticket.put("montoPagado", cuota.getMonto());
            ticket.put("fechaPago", cuota.getFechaPago().toString());
            ticket.put("metodoPago", cuota.getMetodoPago());
            ticket.put("codigoOperacion", cuota.getCodigoOperacion() != null && !cuota.getCodigoOperacion().isEmpty() ? cuota.getCodigoOperacion() : "EFECTIVO");
            ticket.put("serieNumeroVenta", v.getSerie() + "-" + v.getNumero());

            StringBuilder nombreCompleto = new StringBuilder(v.getCliente().getNombres());
            if (v.getCliente().getApellido_Paterno() != null) {
                nombreCompleto.append(" ").append(v.getCliente().getApellido_Paterno());
            }
            if (v.getCliente().getApellido_Materno() != null) {
                nombreCompleto.append(" ").append(v.getCliente().getApellido_Materno());
            }

            ticket.put("clienteNombre", nombreCompleto.toString().trim());
            ticket.put("clienteDni", v.getCliente().getDni());

            double totalPagado = todasLasCuotas.stream()
                    .filter(c -> "PAGADO".equals(c.getEstado()))
                    .mapToDouble(cuota_venta::getMonto).sum();

            ticket.put("totalPagadoHastaHoy", totalPagado);
            ticket.put("saldoRestante", Math.max(0, v.getTotal() - totalPagado));
            ticket.put("totalVentaMaestra", v.getTotal());

            resp.put("ticket", ticket);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // =========================================================================
    // ÚNICO ENDPOINT: APROBACIÓN DE VENTA WEB CON AUDITORÍA MANTENIDA
    // =========================================================================
    // =========================================================================
    // ÚNICO ENDPOINT: APROBACIÓN DE VENTA WEB CON REDUCCIÓN DE STOCK REAL EN BD
    // =========================================================================
    @PostMapping("/aprobar-web/{idVenta}")
    @ResponseBody
    public ResponseEntity<?> aprobarVentaWeb(@PathVariable Integer idVenta, org.springframework.security.core.Authentication auth) {
        try {
            venta v = ventaRepository.findById(idVenta)
                    .orElseThrow(() -> new RuntimeException("La orden de venta especificada no existe."));

            if (v.getEstado() != 3) {
                return ResponseEntity.badRequest().body("Esta venta no se encuentra en estado 'Por Verificar'.");
            }

            List<detalle_venta> detalles = detalleVentaRepository.findByVenta(v);

            // 🌟 PASO OBLIGATORIO: Validar que haya stock suficiente antes de confirmar la aprobación física
            for (detalle_venta det : detalles) {
                if (det.getCantidad() > det.getProductoDetalle().getStock()) {
                    return ResponseEntity.badRequest().body("No se puede aprobar. Stock insuficiente para el producto: "
                            + det.getProductoDetalle().getProducto().getNombre());
                }
            }

            // 🌟 PASO OPERATIVO: Ahora sí, descontamos el stock real de la tienda
            for (detalle_venta det : detalles) {
                producto_detalle prodDet = det.getProductoDetalle();
                prodDet.setStock(prodDet.getStock() - det.getCantidad());
                detalleProductoRepository.save(prodDet);
            }

            String nombreAdmin = (auth != null) ? auth.getName() : "ADMIN_SISTEMA";

            v.setEstado(1); // Cambia a Aprobado
            v.setUsuarioAprobacion(nombreAdmin);
            v.setFechaAprobacion(LocalDateTime.now());
            ventaRepository.save(v);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Venta web verificada y aprobada de manera exitosa!");
            response.put("telefonoCliente", v.getCliente().getTelefono());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la validación: " + e.getMessage());
        }
    }

    // =========================================================================
    // NUEVO ENDPOINT: RECHAZO DE VENTA WEB (SIN TOCAR EL STOCK PORQUE NUNCA SE REDUJO)
    // =========================================================================
    @PostMapping("/rechazar-web/{idVenta}")
    @ResponseBody
    public ResponseEntity<?> rechazarVentaWeb(@PathVariable Integer idVenta, org.springframework.security.core.Authentication auth) {
        try {
            venta v = ventaRepository.findById(idVenta)
                    .orElseThrow(() -> new RuntimeException("La orden de venta especificada no existe."));

            if (v.getEstado() != 3) {
                return ResponseEntity.badRequest().body("Esta venta no se encuentra en estado 'Por Verificar'.");
            }

            String nombreAdmin = (auth != null) ? auth.getName() : "ADMIN_SISTEMA";

            // 🌟 REGLA CORREGIDA: Cambiamos estado a 0 (Rechazado), pero NO tocamos el stock porque seguía libre en tienda
            v.setEstado(0);
            v.setUsuarioAprobacion(nombreAdmin + " (RECHAZADO)");
            v.setFechaAprobacion(LocalDateTime.now());
            ventaRepository.save(v);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Venta web rechazada con éxito! El stock se mantuvo intacto.");
            response.put("telefonoCliente", v.getCliente().getTelefono());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en el rechazo: " + e.getMessage());
        }
    }}