package com.example.proyecto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "producto")
public class producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private BigDecimal precio;

    @Column(columnDefinition = "TEXT")
    private String imagen;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer estado = 1;

    // RELACION
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private categoria categoria;

    @JsonManagedReference
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<producto_detalle> detalles;

    public producto(){}

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(categoria categoria) {
        this.categoria = categoria;
    }

    public List<producto_detalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<producto_detalle> detalles) {
        this.detalles = detalles;
    }
    // Método dinámico modificado: Suma el stock SOLO de los detalles que estén activos (estado == 1)
    public Integer getStock() {
        if (this.detalles == null || this.detalles.isEmpty()) {
            return 0;
        }
        return this.detalles.stream()
                .filter(d -> d != null && d.getEstado() != null && d.getEstado() == 1 && d.getStock() != null)
                .mapToInt(producto_detalle::getStock)
                .sum();
    }
}