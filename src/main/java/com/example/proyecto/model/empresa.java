package com.example.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa")
public class empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Integer idEmpresa;

    private String nombre;
    private String ruc;
    private String direccion;
    private String telefono;
    private String correo;

    private String whatsapp;
    private String facebook;
    private String instagram;
    private String tiktok;

    private String marqueeTexto;

    private String categoria1;
    private String categoria2;
    private String categoria3;
    private String categoria4;

    private String imagenCategorias;

    private String bannerTitulo;
    private String bannerBoton;
    private String bannerLink;
    private String bannerImagen;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer estado = 1;

    public empresa() {
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTiktok() {
        return tiktok;
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getMarqueeTexto() {
        return marqueeTexto;
    }

    public void setMarqueeTexto(String marqueeTexto) {
        this.marqueeTexto = marqueeTexto;
    }

    public String getCategoria1() {
        return categoria1;
    }

    public void setCategoria1(String categoria1) {
        this.categoria1 = categoria1;
    }

    public String getCategoria2() {
        return categoria2;
    }

    public void setCategoria2(String categoria2) {
        this.categoria2 = categoria2;
    }

    public String getCategoria3() {
        return categoria3;
    }

    public void setCategoria3(String categoria3) {
        this.categoria3 = categoria3;
    }

    public String getCategoria4() {
        return categoria4;
    }

    public void setCategoria4(String categoria4) {
        this.categoria4 = categoria4;
    }

    public String getImagenCategorias() {
        return imagenCategorias;
    }

    public void setImagenCategorias(String imagenCategorias) {
        this.imagenCategorias = imagenCategorias;
    }

    public String getBannerTitulo() {
        return bannerTitulo;
    }

    public void setBannerTitulo(String bannerTitulo) {
        this.bannerTitulo = bannerTitulo;
    }

    public String getBannerBoton() {
        return bannerBoton;
    }

    public void setBannerBoton(String bannerBoton) {
        this.bannerBoton = bannerBoton;
    }

    public String getBannerImagen() {
        return bannerImagen;
    }

    public void setBannerImagen(String bannerImagen) {
        this.bannerImagen = bannerImagen;
    }
    public String getBannerLink() {
        return bannerLink;
    }

    public void setBannerLink(String bannerLink) {
        this.bannerLink = bannerLink;
    }
}