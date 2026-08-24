package com.example.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "slider")
public class slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_slider")
    private Integer idSlider;

    private String titulo;

    private String subtitulo;

    private String textoBoton;

    private String linkBoton;

    private String imagen;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer activo = 1;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer estado = 1;

    public slider() {
    }

    public Integer getIdSlider() {
        return idSlider;
    }

    public void setIdSlider(Integer idSlider) {
        this.idSlider = idSlider;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getTextoBoton() {
        return textoBoton;
    }

    public void setTextoBoton(String textoBoton) {
        this.textoBoton = textoBoton;
    }

    public String getLinkBoton() {
        return linkBoton;
    }

    public void setLinkBoton(String linkBoton) {
        this.linkBoton = linkBoton;
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