package com.example.proyecto.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cuota_venta")
public class cuota_venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Integer idCuota;

    @ManyToOne
    @JoinColumn(name = "id_venta")
    private venta venta;

    private Integer nroCuota;

    private Double monto;

    private LocalDate fechaVencimiento;

    private LocalDate fechaPago;

    private String estado;

    // ==========================================
    // NUEVOS CAMPOS ADICIONADOS PARA AUDITORÍA
    // ==========================================
    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "codigo_operacion", length = 100)
    private String codigoOperacion;

    public cuota_venta() {
    }

    public Integer getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Integer idCuota) {
        this.idCuota = idCuota;
    }

    public venta getVenta() {
        return venta;
    }

    public void setVenta(venta venta) {
        this.venta = venta;
    }

    public Integer getNroCuota() {
        return nroCuota;
    }

    public void setNroCuota(Integer nroCuota) {
        this.nroCuota = nroCuota;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // ==========================================
    // GETTERS Y SETTERS DE LOS NUEVOS CAMPOS
    // ==========================================
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public void setCodigoOperacion(String codigoOperacion) {
        this.codigoOperacion = codigoOperacion;
    }
}