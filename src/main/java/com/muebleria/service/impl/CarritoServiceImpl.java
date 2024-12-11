package com.muebleria.service.impl;

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

@Service
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
        Carrito carrito = carritoRepository.findByUsuario(usuario);
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito = carritoRepository.save(carrito); // Guarda el nuevo carrito en la base de datos
        }
        return carrito;
    }

    @Override
    public void agregarProductoAlCarrito(Long carritoId, Long productoId, int cantidad) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Buscar el mueble por ID
        Mueble mueble = muebleRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el producto ya está en el carrito
        ProductoCarrito productoCarrito = new ProductoCarrito();
        productoCarrito.setCarrito(carrito);
        productoCarrito.setMueble(mueble);
        productoCarrito.setCantidad(cantidad);
        productoCarrito.setPrecio(mueble.getPrecio());

        // Guardar la relación en la base de datos
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










