package com.muebleria.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ProductoCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Carrito carrito;  // Relación con el carrito
    
    @ManyToOne
    private Mueble mueble;  // Relación con el producto
    private int cantidad;
    private double precio;  // Precio del producto en el momento de la compra
}
