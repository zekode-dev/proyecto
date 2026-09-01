package com.example.proyecto.dto;

import java.util.List;

public class VentaRequestDTO {

    private String dni;
    private String nombreCliente;
    private String apellidoPaterno; // Agregado correctamente
    private String apellidoMaterno; // Agregado correctamente

    private Double subtotal;
    private Double igv;
    private Double total;

    private String metodoPago;
    private String tipoPago;
    private String codigoOperacion;

    private Integer cuotas;
    private Double montoCuota;
    private String fechaPrimerVencimiento;

    private List<VentaDetalleDTO> detalles;

    // =========================================================================
    // GETTERS Y SETTERS (INCLUYENDO LOS NUEVOS CAMPOS)
    // =========================================================================

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getIgv() {
        return igv;
    }

    public void setIgv(Double igv) {
        this.igv = igv;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public void setCodigoOperacion(String codigoOperacion) {
        this.codigoOperacion = codigoOperacion;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public void setCuotas(Integer cuotas) {
        this.cuotas = cuotas;
    }

    public Double getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(Double montoCuota) {
        this.montoCuota = montoCuota;
    }

    public String getFechaPrimerVencimiento() {
        return fechaPrimerVencimiento;
    }

    public void setFechaPrimerVencimiento(String fechaPrimerVencimiento) {
        this.fechaPrimerVencimiento = fechaPrimerVencimiento;
    }

    public List<VentaDetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalleDTO> detalles) {
        this.detalles = detalles;
    }
}