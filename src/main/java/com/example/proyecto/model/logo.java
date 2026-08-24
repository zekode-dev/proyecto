package com.example.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logo")
public class logo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logo")
    private Integer idLogo;

    private String nombre;

    private String imagen;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer activo = 0;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer estado = 1;

    public logo() {}

    public Integer getIdLogo() {
        return idLogo;
    }

    public void setIdLogo(Integer idLogo) {
        this.idLogo = idLogo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}