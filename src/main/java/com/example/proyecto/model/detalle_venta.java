package com.example.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_venta")
public class detalle_venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Integer idDetalleVenta;

    private Integer cantidad;

    private Double precio;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "id_venta")
    private venta venta;

    // AHORA RELACIONA CON producto_detalle
    @ManyToOne
    @JoinColumn(name = "id_detalle")
    private producto_detalle productoDetalle;

    public detalle_venta(){}

    public Integer getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(Integer idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public venta getVenta() {
        return venta;
    }

    public void setVenta(venta venta) {
        this.venta = venta;
    }

    public producto_detalle getProductoDetalle() {
        return productoDetalle;
    }

    public void setProductoDetalle(producto_detalle productoDetalle) {
        this.productoDetalle = productoDetalle;
    }
}