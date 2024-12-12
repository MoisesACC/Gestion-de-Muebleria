package com.muebleria.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Mueble;
import com.muebleria.entity.ProductoCarrito;
import com.muebleria.entity.Usuario;
import com.muebleria.repository.CarritoRepository;
import com.muebleria.repository.MuebleRepository;
import com.muebleria.repository.ProductoCarritoRepository;
import com.muebleria.repository.UsuarioRepository;
import com.muebleria.service.CarritoService;
import com.muebleria.service.UsuarioService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService{

    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private MuebleRepository muebleRepository;
    
    @Autowired
    private ProductoCarritoRepository productoCarritoRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
    @Override
    public Carrito obtenerCarritoPorUsuario(Usuario usuario) {
        // Buscar carrito activo
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuario_IdAndActivo(usuario.getId(), true);
        if (carritoExistente.isPresent()) {
            return carritoExistente.get();
        }

        // Si no existe un carrito activo, buscar uno inactivo o crear uno nuevo
        Optional<Carrito> carritoInactivo = carritoRepository.findByUsuario_IdAndActivo(usuario.getId(), false);
        if (carritoInactivo.isPresent()) {
            Carrito carrito = carritoInactivo.get();
            carrito.setActivo(true);
            return carritoRepository.save(carrito);
        }

        // Si no existe ningún carrito, se crea uno nuevo
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setUsuario(usuario);
        nuevoCarrito.setActivo(true);
        return carritoRepository.save(nuevoCarrito);
    }

    @Override
    public void agregarProductoAlCarrito(Usuario usuario, Long productoId, int cantidad) {
        // Buscar si ya existe un carrito activo
        Carrito carrito = carritoRepository.findByUsuario_IdAndActivo(usuario.getId(), true)
                .orElseGet(() -> {
                    // Si no existe un carrito activo, buscar el carrito inactivo, o crear uno nuevo
                    Optional<Carrito> carritoInactivo = carritoRepository.findByUsuario_IdAndActivo(usuario.getId(), false);
                    if (carritoInactivo.isPresent()) {
                        // Desactivar el carrito anterior
                        Carrito carritoAntiguo = carritoInactivo.get();
                        carritoAntiguo.setActivo(false);
                        carritoRepository.save(carritoAntiguo);
                    }
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    nuevoCarrito.setActivo(true);
                    return carritoRepository.save(nuevoCarrito);
                });

        // Buscar el producto
        Mueble mueble = muebleRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el producto ya está en el carrito
        ProductoCarrito productoCarrito = productoCarritoRepository
                .findByCarritoAndMueble(carrito, mueble)
                .orElseGet(() -> {
                    ProductoCarrito nuevoProducto = new ProductoCarrito();
                    nuevoProducto.setCarrito(carrito);
                    nuevoProducto.setMueble(mueble);
                    nuevoProducto.setCantidad(0);  // Inicializamos en 0 para incrementar después
                    nuevoProducto.setPrecio(mueble.getPrecio());
                    return nuevoProducto;
                });

        // Actualizar la cantidad del producto en el carrito
        productoCarrito.setCantidad(productoCarrito.getCantidad() + cantidad);
        productoCarritoRepository.save(productoCarrito);
    }


    @Override
    public int obtenerCantidadProductosEnCarrito(Long id) {
        // Lógica para obtener la cantidad de productos en el carrito
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        return carrito.getProductos().size();  // Suponiendo que tienes una lista de productos en el carrito
    }

	@Override
    public Carrito guardarCarrito(Carrito carrito) {
        return carritoRepository.save(carrito);  // Guarda el carrito en la base de datos
    }
	
}










