package com.muebleria.service;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Usuario;

public interface CarritoService {
    Carrito obtenerCarritoPorUsuario(Usuario usuario);  // Cambiado para recibir un Usuario
    void agregarProductoAlCarrito(Long carritoId, Long productoId, int cantidad);
    int obtenerCantidadProductosEnCarrito(Long id);
    Carrito guardarCarrito(Carrito carrito);
}