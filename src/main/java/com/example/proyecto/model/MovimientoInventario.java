package com.example.proyecto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto_detalle", nullable = false)
    private producto_detalle productoDetalle;

    @Column(nullable = false, length = 20)
    private String tipoMovimiento; // "ENTRADA" o "SALIDA"

    @Column(nullable = false, length = 50)
    private String motivo; // "COMPRA MERCADERIA", "MERMA / DAÑADO", "SALIDA EXTRAORDINARIA"

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // 🌟 NUEVO CAMPO ADICIONAL PARA EL SUSTENTO LOGÍSTICO
    @Column(name = "observacion", length = 255)
    private String observacion;

    public MovimientoInventario() {}

    // =====================================
    // GETTERS Y SETTERS COMPLEMENTARIOS
    // =====================================
    public Integer getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(Integer idMovimiento) { this.idMovimiento = idMovimiento; }

    public producto_detalle getProductoDetalle() { return productoDetalle; }
    public void setProductoDetalle(producto_detalle productoDetalle) { this.productoDetalle = productoDetalle; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}