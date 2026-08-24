package com.example.proyecto.controller;

import com.example.proyecto.model.usuario;
import com.example.proyecto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class LoginControlador {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. RECIBE EL CORREO DEL MODAL Y ENVÍA EL EMAIL REAL
    @PostMapping("/recuperar-password")
    public String procesarRecuperacionPassword(@RequestParam("correoRecuperacion") String correo,
                                               RedirectAttributes redirectAttributes) {

        var usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            usuario user = usuarioOpt.get();

            String token = UUID.randomUUID().toString();
            user.setTokenRecuperacion(token);
            user.setFechaExpiracionToken(LocalDateTime.now().plusMinutes(15));
            usuarioRepository.save(user);

            String urlRecuperacion = "http://localhost:8081/cambiar-password?token=" + token;

            try {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo(correo);
                email.setSubject("RESTABLECER CONTRASEÑA - TIENDA URBANA");
                email.setText("Hola " + user.getNombres() + ",\n\nHas solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para modificarla (Este enlace expira en 15 minutos):\n\n" + urlRecuperacion + "\n\nSi no lo solicitaste, ignora este correo.");

                mailSender.send(email);
                redirectAttributes.addAttribute("recuperacionExito", "true");
            } catch (Exception e) {
                System.out.println("Error al enviar correo: " + e.getMessage());
                redirectAttributes.addAttribute("error", "mail_error");
            }
        } else {
            redirectAttributes.addAttribute("recuperacionExito", "true");
        }
        return "redirect:/login";
    }

    // 2. ABRE LA PÁGINA NUEVA VALIDANDO EL TOKEN
    @GetMapping("/cambiar-password")
    public String mostrarPantallaCambio(@RequestParam("token") String token, Model model) {

        var usuarioOpt = usuarioRepository.findAll().stream()
                .filter(u -> u != null && token.equals(u.getTokenRecuperacion())
                        && u.getFechaExpiracionToken() != null
                        && u.getFechaExpiracionToken().isAfter(LocalDateTime.now()))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            return "redirect:/login?error=token_invalido";
        }

        model.addAttribute("token", token);
        return "cambiar-password";
    }

    // 3. RECIBE LA NUEVA CLAVE, LA ENCRIPTA Y ACTUALIZA
    @PostMapping("/actualizar-password")
    public String actualizarPassword(@RequestParam("token") String token,
                                     @RequestParam("password") String nuevaPassword,
                                     RedirectAttributes redirectAttributes) {

        var usuarioOpt = usuarioRepository.findAll().stream()
                .filter(u -> u != null && token.equals(u.getTokenRecuperacion()))
                .findFirst();

        if (usuarioOpt.isPresent()) {
            usuario user = usuarioOpt.get();

            String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
            user.setPassword(passwordEncriptada);

            user.setTokenRecuperacion(null);
            user.setFechaExpiracionToken(null);

            usuarioRepository.save(user);
            redirectAttributes.addAttribute("passwordActualizada", "true");
        } else {
            return "redirect:/login?error=token_invalido";
        }

        return "redirect:/login";
    }
}