package com.example.proyecto.controller;

import com.example.proyecto.model.empresa;
import com.example.proyecto.model.logo;
import com.example.proyecto.model.slider;
import com.example.proyecto.repository.EmpresaRepository;
import com.example.proyecto.repository.LogoRepository;
import com.example.proyecto.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ConfiguracionController {

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    // =====================================================
    // VISTA
    // =====================================================

    @GetMapping("/configuracion")
    public String configuracion(Model model) {

        model.addAttribute(
                "sliders",
                sliderRepository.findAll()
        );

        model.addAttribute(
                "logos",
                logoRepository.findAll()
        );

        empresa empresaData;

        if (empresaRepository.count() > 0) {

            empresaData =
                    empresaRepository.findAll().get(0);

        } else {

            empresaData = new empresa();

        }

        model.addAttribute(
                "empresa",
                empresaData
        );

        return "configuracion";
    }

    // =====================================================
    // GUARDAR SLIDER
    // =====================================================

    @PostMapping("/configuracion/slider/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarSlider(

            @RequestParam String titulo,
            @RequestParam String subtitulo,
            @RequestParam(required = false) String textoBoton,
            @RequestParam(required = false) String linkBoton,
            @RequestParam MultipartFile imagen

    ) {

        try {

            slider s = new slider();

            s.setTitulo(titulo);
            s.setSubtitulo(subtitulo);
            s.setTextoBoton(textoBoton);
            s.setLinkBoton(linkBoton);
            s.setEstado(1);
            sliderRepository.save(s);

            // =========================================
            // CARPETA
            // =========================================

            String carpetaBase =
                    "img/sliders/" + s.getIdSlider();

            File carpeta = new File(carpetaBase);

            if (!carpeta.exists()) {

                carpeta.mkdirs();

            }

            // =========================================
            // IMAGEN
            // =========================================

            String extension =
                    imagen.getOriginalFilename()
                            .substring(
                                    imagen.getOriginalFilename()
                                            .lastIndexOf(".")
                            );

            String nombreImagen =
                    "slider" + extension;

            Path ruta =
                    Paths.get(
                            carpetaBase,
                            nombreImagen
                    );

            Files.copy(
                    imagen.getInputStream(),
                    ruta
            );

            s.setImagen(
                    "/" + carpetaBase + "/" + nombreImagen
            );

            sliderRepository.save(s);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }

    }

    // =====================================================
    // ACTIVAR SLIDER
    // =====================================================

    @PostMapping("/configuracion/slider/activar")
    @ResponseBody
    public ResponseEntity<?> activarSlider(
            @RequestParam Integer idSlider
    ) {

        try {

            slider s =
                    sliderRepository.findById(idSlider)
                            .orElseThrow();

            s.setEstado(1);

            sliderRepository.save(s);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("error");
        }

    }

    // =====================================================
    // DESACTIVAR SLIDER
    // =====================================================

    @PostMapping("/configuracion/slider/desactivar")
    @ResponseBody
    public ResponseEntity<?> desactivarSlider(
            @RequestParam Integer idSlider
    ) {

        try {

            slider s =
                    sliderRepository.findById(idSlider)
                            .orElseThrow();

            s.setEstado(0);

            sliderRepository.save(s);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("error");
        }

    }

    // =====================================================
// ACTUALIZAR SLIDER
// =====================================================

    @PostMapping("/configuracion/slider/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarSlider(

            @RequestParam Integer idSlider,
            @RequestParam String titulo,
            @RequestParam String subtitulo,
            @RequestParam String textoBoton,
            @RequestParam String linkBoton,
            @RequestParam(required = false)
            MultipartFile imagen

    ) {

        try {

            slider s =
                    sliderRepository.findById(idSlider)
                            .orElseThrow();

            s.setTitulo(titulo);
            s.setSubtitulo(subtitulo);
            s.setTextoBoton(textoBoton);
            s.setLinkBoton(linkBoton);

            // =====================================
            // NUEVA IMAGEN
            // =====================================

            if (imagen != null &&
                    !imagen.isEmpty()) {

                String carpetaBase =
                        "img/sliders/" + s.getIdSlider();

                String extension =
                        imagen.getOriginalFilename()
                                .substring(
                                        imagen.getOriginalFilename()
                                                .lastIndexOf(".")
                                );

                String nombreImagen =
                        "slider" + extension;

                Path ruta =
                        Paths.get(
                                carpetaBase,
                                nombreImagen
                        );

                Files.copy(
                        imagen.getInputStream(),
                        ruta,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                s.setImagen(
                        "/" + carpetaBase + "/" + nombreImagen
                                + "?v=" + System.currentTimeMillis()
                );

            }

            sliderRepository.save(s);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");

        }

    }

    // =====================================================
// GUARDAR LOGO
// =====================================================

    @PostMapping("/configuracion/logo/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarLogo(

            @RequestParam String nombre,
            @RequestParam MultipartFile imagen

    ) {

        try {

            logo l = new logo();

            l.setNombre(nombre);
            l.setEstado(1);

            logoRepository.save(l);

            // =====================================
            // CARPETA
            // =====================================

            String carpetaBase =
                    "img/logos/" + l.getIdLogo();

            File carpeta = new File(carpetaBase);

            if (!carpeta.exists()) {

                carpeta.mkdirs();

            }

            // =====================================
            // IMAGEN
            // =====================================

            String extension =
                    imagen.getOriginalFilename()
                            .substring(
                                    imagen.getOriginalFilename()
                                            .lastIndexOf(".")
                            );

            String nombreImagen =
                    "logo" + extension;

            Path ruta =
                    Paths.get(
                            carpetaBase,
                            nombreImagen
                    );

            Files.copy(
                    imagen.getInputStream(),
                    ruta
            );

            l.setImagen(
                    "/" + carpetaBase + "/" + nombreImagen
                            + "?v=" + System.currentTimeMillis()
            );
            logoRepository.save(l);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");

        }

    }
    // =====================================================
// ACTUALIZAR LOGO
// =====================================================

    @PostMapping("/configuracion/logo/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarLogo(

            @RequestParam Integer idLogo,
            @RequestParam String nombre,
            @RequestParam(required = false)
            MultipartFile imagen

    ) {

        try {

            logo l =
                    logoRepository.findById(idLogo)
                            .orElseThrow();

            l.setNombre(nombre);

            // =====================================
            // NUEVA IMAGEN
            // =====================================

            if (imagen != null &&
                    !imagen.isEmpty()) {

                String carpetaBase =
                        "img/logos/" + l.getIdLogo();

                String extension =
                        imagen.getOriginalFilename()
                                .substring(
                                        imagen.getOriginalFilename()
                                                .lastIndexOf(".")
                                );

                String nombreImagen =
                        "logo" + extension;

                Path ruta =
                        Paths.get(
                                carpetaBase,
                                nombreImagen
                        );

                Files.copy(
                        imagen.getInputStream(),
                        ruta,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                l.setImagen(
                        "/" + carpetaBase + "/" + nombreImagen
                                + "?v=" + System.currentTimeMillis()
                );

            }

            logoRepository.save(l);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");

        }

    }
    // =====================================================
// ACTIVAR LOGO
// =====================================================

    @PostMapping("/configuracion/logo/activar")
    @ResponseBody
    public ResponseEntity<?> activarLogo(
            @RequestParam Integer idLogo
    ) {

        try {

            // =====================================
            // DESACTIVAR TODOS
            // =====================================

            List<logo> logos =
                    logoRepository.findAll();

            for(logo item : logos){

                item.setEstado(0);

                logoRepository.save(item);

            }

            // =====================================
            // ACTIVAR SOLO EL SELECCIONADO
            // =====================================

            logo l =
                    logoRepository.findById(idLogo)
                            .orElseThrow();

            l.setEstado(1);

            logoRepository.save(l);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }

    }
// =====================================================
// DESACTIVAR LOGO
// =====================================================

    @PostMapping("/configuracion/logo/desactivar")
    @ResponseBody
    public ResponseEntity<?> desactivarLogo(
            @RequestParam Integer idLogo
    ) {

        try {

            logo l =
                    logoRepository.findById(idLogo)
                            .orElseThrow();

            l.setEstado(0);

            logoRepository.save(l);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }

    }


    // =====================================================
// GUARDAR EMPRESA
// =====================================================

    @PostMapping("/configuracion/empresa/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarEmpresa(

            @RequestParam String nombre,
            @RequestParam String telefono,
            @RequestParam String correo,
            @RequestParam String whatsapp,
            @RequestParam String direccion,
            @RequestParam String facebook,
            @RequestParam String instagram,
            @RequestParam String tiktok,

            @RequestParam String marqueeTexto,

            @RequestParam String categoria1,
            @RequestParam String categoria2,
            @RequestParam String categoria3,
            @RequestParam String categoria4,

            @RequestParam String bannerTitulo,
            @RequestParam String bannerBoton,
            @RequestParam String bannerLink,

            @RequestParam(required = false)
            MultipartFile imagenCategorias,

            @RequestParam(required = false)
            MultipartFile bannerImagen

    ) {

        try {

            empresa e;

            if (empresaRepository.count() > 0) {

                e = empresaRepository.findAll().get(0);

            } else {

                e = new empresa();

            }

            // =====================================
            // DATOS
            // =====================================

            e.setNombre(nombre);
            e.setTelefono(telefono);
            e.setCorreo(correo);
            e.setWhatsapp(whatsapp);
            e.setDireccion(direccion);

            e.setFacebook(facebook);
            e.setInstagram(instagram);
            e.setTiktok(tiktok);

            e.setMarqueeTexto(marqueeTexto);

            e.setCategoria1(categoria1);
            e.setCategoria2(categoria2);
            e.setCategoria3(categoria3);
            e.setCategoria4(categoria4);

            e.setBannerTitulo(bannerTitulo);
            e.setBannerBoton(bannerBoton);
            e.setBannerLink(bannerLink);

            empresaRepository.save(e);

            // =====================================
            // CARPETA
            // =====================================

            String carpetaBase =
                    "img/empresa/" + e.getIdEmpresa();

            File carpeta = new File(carpetaBase);

            if (!carpeta.exists()) {

                carpeta.mkdirs();

            }

            // =====================================
            // IMAGEN CATEGORIAS
            // =====================================

            if (imagenCategorias != null &&
                    !imagenCategorias.isEmpty()) {

                String extension =
                        imagenCategorias.getOriginalFilename()
                                .substring(
                                        imagenCategorias.getOriginalFilename()
                                                .lastIndexOf(".")
                                );

                String nombreImagen =
                        "categorias" + extension;

                Path ruta =
                        Paths.get(
                                carpetaBase,
                                nombreImagen
                        );

                Files.copy(
                        imagenCategorias.getInputStream(),
                        ruta,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                e.setImagenCategorias(
                        "/" + carpetaBase + "/" + nombreImagen
                                + "?v=" + System.currentTimeMillis()
                );

            }

            // =====================================
            // IMAGEN BANNER
            // =====================================

            if (bannerImagen != null &&
                    !bannerImagen.isEmpty()) {

                String extension =
                        bannerImagen.getOriginalFilename()
                                .substring(
                                        bannerImagen.getOriginalFilename()
                                                .lastIndexOf(".")
                                );

                String nombreImagen =
                        "banner" + extension;

                Path ruta =
                        Paths.get(
                                carpetaBase,
                                nombreImagen
                        );

                Files.copy(
                        bannerImagen.getInputStream(),
                        ruta,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                e.setBannerImagen(
                        "/" + carpetaBase + "/" + nombreImagen
                                + "?v=" + System.currentTimeMillis()
                );

            }

            empresaRepository.save(e);

            return ResponseEntity.ok("ok");

        } catch (Exception ex) {

            ex.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("error");
        }

    }
}