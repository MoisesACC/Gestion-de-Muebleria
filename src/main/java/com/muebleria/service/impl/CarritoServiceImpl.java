package com.muebleria.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Mueble;
import com.muebleria.entity.ProductoCarrito;
import com.muebleria.entity.Usuario;
import com.muebleria.repository.CarritoRepository;
import com.muebleria.repository.MuebleRepository;
import com.muebleria.repository.ProductoCarritoRepository;
import com.muebleria.service.CarritoService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private MuebleRepository muebleRepository;

    @Autowired
    private ProductoCarritoRepository productoCarritoRepository;

    @Override
    public Carrito obtenerCarritoPorUsuario(Usuario usuario) {
        // Buscar carrito activo del usuario
        return carritoRepository.findByUsuario_IdAndActivo(usuario.getId(), true)
                .orElseGet(() -> {
                    // Si no existe, se crea uno nuevo
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    nuevoCarrito.setActivo(true);
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    @Override
    public void agregarProductoAlCarrito(Usuario usuario, Long productoId, int cantidad) {
        // Obtener el carrito activo del usuario
        Carrito carrito = obtenerCarritoPorUsuario(usuario);

        // Buscar el producto en el repositorio
        Mueble mueble = muebleRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el producto ya está en el carrito
        ProductoCarrito productoCarrito = productoCarritoRepository.findByCarritoAndMueble(carrito, mueble)
                .orElseGet(() -> {
                    ProductoCarrito nuevoProducto = new ProductoCarrito();
                    nuevoProducto.setCarrito(carrito);
                    nuevoProducto.setMueble(mueble);
                    nuevoProducto.setCantidad(0); // Inicializar con 0
                    nuevoProducto.setPrecio(mueble.getPrecio());
                    return nuevoProducto;
                });

        // Incrementar la cantidad del producto
        productoCarrito.setCantidad(productoCarrito.getCantidad() + cantidad);
        productoCarritoRepository.save(productoCarrito);
    }

    @Override
    public int obtenerCantidadProductosEnCarrito(Long id) {
        // Buscar carrito por ID
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Devolver el tamaño de la lista de productos
        return carrito.getProductos().size(); // Suponiendo que `getProductos` devuelve una lista de ProductoCarrito
    }

    @Override
    public Carrito guardarCarrito(Carrito carrito) {
        // Guardar el carrito en la base de datos
        return carritoRepository.save(carrito);
    }

    @Override
    public void eliminarProductoDelCarrito(Usuario usuario, Long productoId) {
        // Obtener el carrito activo del usuario
        Carrito carrito = obtenerCarritoPorUsuario(usuario);

        // Buscar el producto en el carrito
        Mueble mueble = muebleRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        ProductoCarrito productoCarrito = productoCarritoRepository.findByCarritoAndMueble(carrito, mueble)
                .orElseThrow(() -> new RuntimeException("Producto no está en el carrito"));

        // Eliminar el producto del carrito
        productoCarritoRepository.delete(productoCarrito);

        // Recalcular el total del carrito
        double nuevoTotal = carrito.getProductos().stream()
            .filter(p -> !p.getMueble().getId().equals(productoId)) // Excluir el producto eliminado
            .mapToDouble(p -> p.getPrecio() * p.getCantidad())
            .sum();

        carrito.setTotal(nuevoTotal);

        // Guardar el carrito actualizado
        carritoRepository.save(carrito);
    }


    @Override
    public void vaciarCarrito(Usuario usuario) {
        // Obtener el carrito activo del usuario
        Carrito carrito = obtenerCarritoPorUsuario(usuario);

        // Eliminar todos los productos del carrito
        productoCarritoRepository.deleteAllByCarrito(carrito);
    }

    public void recalcularTotal(Carrito carrito) {
        double total = carrito.getProductos().stream()
                .mapToDouble(p -> p.getCantidad() * p.getPrecio())
                .sum();
        carrito.setTotal(total);
        carritoRepository.save(carrito); // Guarda el total actualizado en la base de datos
    }

    @Override
    public double calcularTotalCarrito(Usuario usuario) {
        Carrito carrito = obtenerCarritoPorUsuario(usuario);
        return carrito.getProductos().stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                .sum();
    }


}
