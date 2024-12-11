package com.muebleria.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FacturaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Factura factura;  // Relación con la factura
    
    @ManyToOne
    private Mueble mueble;  // Relación con el producto
    
    private int cantidad;
    private BigDecimal precio;
    private BigDecimal total;  // Precio * cantidad
}
