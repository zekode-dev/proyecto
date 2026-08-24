package com.example.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rol_permiso")
public class rol_permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_permiso")
    private Integer idRolPermiso;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private rol rol;

    @ManyToOne
    @JoinColumn(name = "id_permiso")
    private permiso permiso;

    public rol_permiso(){}

    public Integer getIdRolPermiso() {
        return idRolPermiso;
    }

    public void setIdRolPermiso(Integer idRolPermiso) {
        this.idRolPermiso = idRolPermiso;
    }

    public rol getRol() {
        return rol;
    }

    public void setRol(rol rol) {
        this.rol = rol;
    }

    public permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(permiso permiso) {
        this.permiso = permiso;
    }
}