package com.example.proyecto.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rol")
public class rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    private String nombre;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer estado = 1;


    @ManyToMany
    @JoinTable(
            name = "rol_permiso",
            joinColumns = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private List<permiso> permisos;

    public rol() {}

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public List<permiso> getPermisos() { return permisos; }
    public void setPermisos(List<permiso> permisos) { this.permisos = permisos; }
}