package com.example.proyecto.config;

import com.example.proyecto.model.rol;
import com.example.proyecto.model.usuario;
import com.example.proyecto.model.permiso;

import com.example.proyecto.repository.RolRepository;
import com.example.proyecto.repository.UsuarioRepository;
import com.example.proyecto.repository.PermisoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PermisoRepository permisoRepository,
            BCryptPasswordEncoder encoder) {

        return args -> {


            // CREAR PERMISOS

            if (permisoRepository.count() == 0) {

                permisoRepository.saveAll(List.of(
                        crearPermiso("VER_USUARIOS"),
                        crearPermiso("VER_PERMISOS"),
                        crearPermiso("VER_ROLES"),
                        crearPermiso("VER_PRODUCTOS"),
                        crearPermiso("VER_DASHBOARD"),
                        crearPermiso("VER_CATEGORIAS"),
                        crearPermiso("VER_VENTAS"),
                        crearPermiso("VER_CLIENTES"),
                        crearPermiso("VER_CONFIGURACION")

                        ));

                System.out.println(" Permisos creados");
            }

            List<permiso> todosPermisos = permisoRepository.findAll();


            // CREAR ROL ADMIN

            rol adminRol = rolRepository.findByNombre("ADMINISTRADOR")
                    .orElseGet(() -> {
                        rol r = new rol();
                        r.setNombre("ADMINISTRADOR");
                        r.setEstado(1);
                        r.setPermisos(todosPermisos);
                        return rolRepository.save(r);
                    });


            // CREAR USUARIO ADMIN

            if (usuarioRepository.findByUsuario("admin").isEmpty()) {

                usuario admin = new usuario();
                admin.setDni("72372644");
                admin.setNombres("CESAR CIEZA ESPINOZA");
                admin.setUsuario("admin");
                admin.setCorreo("admin@gmail.com");
                admin.setPassword(encoder.encode("123"));
                admin.setTelefono("919660485");
                admin.setEstado(1);
                admin.setRol(adminRol);

                usuarioRepository.save(admin);

                System.out.println(" Usuario ADMIN creado");
            }
        };
    }

    private permiso crearPermiso(String nombre) {
        permiso p = new permiso();
        p.setNombre(nombre.toUpperCase());
        p.setEstado(1);
        return p;
    }
}