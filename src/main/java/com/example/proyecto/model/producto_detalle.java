    package com.example.proyecto.model;

    import jakarta.persistence.*;
    import com.fasterxml.jackson.annotation.JsonIgnore;

    @Entity
    @Table(name = "producto_detalle")
    public class producto_detalle {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_detalle")
        private Integer idDetalle;

        private String talla;

        private String color;

        private Integer stock;

        private Integer stockMinimo;

        @Column(columnDefinition = "TINYINT DEFAULT 1")
        private Integer estado = 1;

        // RELACION
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "id_producto")
        private producto producto;

        public producto_detalle(){}

        public Integer getIdDetalle() {
            return idDetalle;
        }

        public void setIdDetalle(Integer idDetalle) {
            this.idDetalle = idDetalle;
        }

        public String getTalla() {
            return talla;
        }

        public void setTalla(String talla) {
            this.talla = talla;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public Integer getStockMinimo() {
            return stockMinimo;
        }

        public void setStockMinimo(Integer stockMinimo) {
            this.stockMinimo = stockMinimo;
        }

        public Integer getEstado() {
            return estado;
        }

        public void setEstado(Integer estado) {
            this.estado = estado;
        }

        public producto getProducto() {
            return producto;
        }

        public void setProducto(producto producto) {
            this.producto = producto;
        }
    }