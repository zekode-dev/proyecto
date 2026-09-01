package com.example.proyecto.controller;

import com.example.proyecto.model.categoria;
import com.example.proyecto.model.producto;
import com.example.proyecto.model.producto_detalle;

import com.example.proyecto.repository.CategoriaRepository;
import com.example.proyecto.repository.ProductoRepository;
import com.example.proyecto.repository.DetalleProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import java.math.BigDecimal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoControlador {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private DetalleProductoRepository detalleProductoRepository;

    // ======================================================
    // LISTAR
    // ======================================================

    @GetMapping
    public String listar(Model model) {

        List<producto> productos =
                productoRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "idProducto")
                );

        List<categoria> categorias =
                categoriaRepository.findByEstado(1);

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);

        return "producto";
    }

    // ======================================================
    // GUARDAR PRODUCTO
    // ======================================================

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardar(

            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam Integer idCategoria,

            @RequestParam List<String> talla,
            @RequestParam List<String> color,
            @RequestParam List<Integer> stockDetalle,
            @RequestParam List<Integer> stockMinimoDetalle,

            @RequestParam(required = false)
            List<MultipartFile> imagenDetalle,

            @RequestParam("imagenes")
            MultipartFile[] imagenes
    ) {

        try {

            categoria categoria =
                    categoriaRepository.findById(idCategoria)
                            .orElseThrow();

            // ======================================================
            // GUARDAR PRODUCTO
            // ======================================================

            producto p = new producto();

            p.setNombre(nombre);

            p.setDescripcion(descripcion);

            p.setPrecio(precio);

            p.setCategoria(categoria);

            p.setEstado(1);

            productoRepository.save(p);

            // ======================================================
            // CREAR CARPETA
            // ======================================================

            String carpetaBase =
                    "img/productos/" + p.getIdProducto();

            File carpeta = new File(carpetaBase);

            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            // ======================================================
            // GUARDAR IMAGENES
            // ======================================================

            List<String> rutas = new ArrayList<>();

            int contador = 1;

            for (MultipartFile imagen : imagenes) {

                if (!imagen.isEmpty()) {

                    String extension =
                            imagen.getOriginalFilename()
                                    .substring(
                                            imagen.getOriginalFilename()
                                                    .lastIndexOf(".")
                                    );

                    String nombreImagen =
                            "img" + contador + extension;

                    Path ruta =
                            Paths.get(
                                    carpetaBase,
                                    nombreImagen
                            );

                    Files.write(
                            ruta,
                            imagen.getBytes()
                    );

                    rutas.add(
                            "/" + carpetaBase + "/" + nombreImagen
                    );

                    contador++;
                }
            }

            p.setImagen(String.join(",", rutas));

            productoRepository.save(p);

            // ======================================================
            // GUARDAR DETALLES
            // ======================================================

            for (int i = 0; i < talla.size(); i++) {

                producto_detalle d =
                        new producto_detalle();

                d.setProducto(p);

                d.setTalla(
                        talla.get(i)
                );

                d.setColor(
                        color.get(i)
                );

                d.setStock(
                        stockDetalle.get(i)
                );

                d.setStockMinimo(
                        stockMinimoDetalle.get(i)
                );

                d.setEstado(1);

                detalleProductoRepository.save(d);
            }

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }
    }

    // ======================================================
    // ACTUALIZAR PRODUCTO
    // ======================================================

    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizar(

            @RequestParam Integer idProducto,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam Integer idCategoria,
            @RequestParam List<String> talla,
            @RequestParam List<String> color,
            @RequestParam List<Integer> stockDetalle,
            @RequestParam List<Integer> stockMinimoDetalle,
            @RequestParam(required = false)
            List<Integer> idDetalle,

            @RequestParam(required = false)
            MultipartFile imagen1,

            @RequestParam(required = false)
            MultipartFile imagen2,

            @RequestParam(required = false)
            MultipartFile imagen3

    ) {

        try {

            producto p =
                    productoRepository.findById(idProducto)
                            .orElseThrow();

            categoria categoria =
                    categoriaRepository.findById(idCategoria)
                            .orElseThrow();

            // ======================================================
            // ACTUALIZAR DATOS
            // ======================================================

            p.setNombre(nombre);

            p.setDescripcion(descripcion);

            p.setPrecio(precio);

            p.setCategoria(categoria);

            // ======================================================
            // CARPETA
            // ======================================================

            String carpetaBase =
                    "img/productos/" + p.getIdProducto();

            File carpeta = new File(carpetaBase);

            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            // ======================================================
            // IMAGENES ACTUALES
            // ======================================================

            String[] imagenesActuales =
                    new String[3];

            if (p.getImagen() != null &&
                    !p.getImagen().isEmpty()) {

                String[] temp =
                        p.getImagen().split(",");

                for (int i = 0; i < temp.length; i++) {

                    imagenesActuales[i] = temp[i];
                }
            }

            // ======================================================
            // NUEVAS IMAGENES
            // ======================================================

            MultipartFile[] nuevas =
                    {imagen1, imagen2, imagen3};

            for (int i = 0; i < nuevas.length; i++) {

                MultipartFile archivo = nuevas[i];

                if (archivo != null &&
                        !archivo.isEmpty()) {

                    String extension =
                            archivo.getOriginalFilename()
                                    .substring(
                                            archivo.getOriginalFilename()
                                                    .lastIndexOf(".")
                                    );

                    String nombreImagen =
                            "img" + (i + 1) + extension;

                    Path ruta =
                            Paths.get(
                                    carpetaBase,
                                    nombreImagen
                            );

                    // ELIMINAR ANTIGUA

                    File[] archivos =
                            carpeta.listFiles();

                    if (archivos != null) {

                        for (File f : archivos) {

                            if (f.getName()
                                    .startsWith(
                                            "img" + (i + 1)
                                    )) {

                                f.delete();
                            }
                        }
                    }

                    // GUARDAR NUEVA

                    Files.copy(
                            archivo.getInputStream(),
                            ruta,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );

                    imagenesActuales[i] =
                            "/" + carpetaBase + "/" + nombreImagen;
                }
            }

            // ======================================================
            // GUARDAR RUTAS
            // ======================================================

            List<String> rutasFinales =
                    new ArrayList<>();

            for (String ruta : imagenesActuales) {

                if (ruta != null) {

                    rutasFinales.add(ruta);
                }
            }

            p.setImagen(
                    String.join(",", rutasFinales)
            );

            productoRepository.save(p);
            // ======================================
// ACTUALIZAR DETALLES
// ======================================

            List<producto_detalle> detallesExistentes =
                    detalleProductoRepository.findByProducto(p);

            detalleProductoRepository.deleteAll(detallesExistentes);

            for (int i = 0; i < talla.size(); i++) {

                producto_detalle d =
                        new producto_detalle();

                d.setProducto(p);

                d.setTalla(
                        talla.get(i)
                );

                d.setColor(
                        color.get(i)
                );

                d.setStock(
                        stockDetalle.get(i)
                );

                d.setStockMinimo(
                        stockMinimoDetalle.get(i)
                );

                d.setEstado(1);

                detalleProductoRepository.save(d);
            }

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }
    }

    // ======================================================
    // ELIMINAR LOGICO
    // ======================================================

    @PostMapping("/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminar(

            Integer idProducto

    ) {

        try {

            producto p =
                    productoRepository.findById(idProducto)
                            .orElseThrow();

            p.setEstado(0);

            productoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("error");
        }
    }

    // ======================================================
    // REACTIVAR
    // ======================================================

    @PostMapping("/reactivar")
    @ResponseBody
    public ResponseEntity<?> reactivar(

            Integer idProducto

    ) {

        try {

            producto p =
                    productoRepository.findById(idProducto)
                            .orElseThrow();

            p.setEstado(1);

            productoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("error");
        }
    }

    //==============================================
    // PARA PORTADA
    //=============================================
    @PostMapping("/portada")
    @ResponseBody
    public ResponseEntity<?> cambiarPortada(

            @RequestParam Integer idProducto,
            @RequestParam String imagen

    ) {

        try {

            producto p =
                    productoRepository.findById(idProducto)
                            .orElseThrow();

            List<String> imagenes =
                    new ArrayList<>(
                            Arrays.asList(
                                    p.getImagen().split(",")
                            )
                    );

            imagenes.remove(imagen);

            imagenes.add(0, imagen);

            p.setImagen(
                    String.join(",", imagenes)
            );

            productoRepository.save(p);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }
    }
    // ======================================================
// OBTENER TALLAS Y STOCK POR PRODUCTO
// ======================================================

    @GetMapping("/detalles/{idProducto}")
    @ResponseBody
    public ResponseEntity<?> obtenerDetalles(
            @PathVariable Integer idProducto
    ) {

        try {

            producto p =
                    productoRepository.findById(idProducto)
                            .orElseThrow();

            List<producto_detalle> detalles =
                    detalleProductoRepository
                            .findByProductoAndEstado(
                                    p,
                                    1
                            );

            return ResponseEntity.ok(detalles);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("error");
        }
    }
}