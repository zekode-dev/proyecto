package com.example.proyecto.repository;

import com.example.proyecto.model.rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<rol, Integer> {

    Optional<rol> findByNombre(String nombre);

    List<rol> findByEstado(Integer estado);

    // 🔥 NUEVA CONSULTA: Cuenta usuarios activos (estado 1) asociados a este rol
    @Query("SELECT COUNT(u) FROM usuario u WHERE u.rol.idRol = :idRol AND u.estado = 1")
    long contarUsuariosActivosPorRol(@Param("idRol") Integer idRol);
}