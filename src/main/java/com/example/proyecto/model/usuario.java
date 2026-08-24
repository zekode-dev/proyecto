package com.example.proyecto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "usuario")
public class usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String dni;
    private String nombres;
    private String usuario;
    private String correo;
    private String password;
    private String telefono;
    private Integer estado;

    // CAMPOS NUEVOS PARA LA RECUPERACIÓN POR CORREO
    private String tokenRecuperacion;
    private LocalDateTime fechaExpiracionToken;

    // TIMESTAMP AUTOMÁTICO
    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    //  RELACIÓN CON ROL
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private rol rol;

    public usuario() {}

    // GETTERS Y SETTERS ORIGINALES
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public rol getRol() { return rol; }
    public void setRol(rol rol) { this.rol = rol; }

    // GETTERS Y SETTERS DE LOS NUEVOS CAMPOS
    public String getTokenRecuperacion() { return tokenRecuperacion; }
    public void setTokenRecuperacion(String tokenRecuperacion) { this.tokenRecuperacion = tokenRecuperacion; }

    public LocalDateTime getFechaExpiracionToken() { return fechaExpiracionToken; }
    public void setFechaExpiracionToken(LocalDateTime fechaExpiracionToken) { this.fechaExpiracionToken = fechaExpiracionToken; }
}