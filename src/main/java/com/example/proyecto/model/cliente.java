package com.example.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    private String nombres;

    private String apellido_materno;
    private String apellido_paterno;

    private String dni;

    private String telefono;

    private String correo;

    private String direccion;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer estado = 1;

    public cliente() {}

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    // ==========================================
    // GETTER Y SETTER: APELLIDO MATERNO (CORREGIDO)
    // ==========================================
    public String getApellido_Materno() {
        return apellido_materno;
    }

    public void setApellido_Materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    // ==========================================
    // GETTER Y SETTER: APELLIDO PATERNO (CORREGIDO)
    // ==========================================
    public String getApellido_Paterno() {
        return apellido_paterno;
    }

    public void setApellido_Paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}