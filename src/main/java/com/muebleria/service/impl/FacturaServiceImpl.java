package com.muebleria.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Factura;
import com.muebleria.entity.FacturaDetalle;
import com.muebleria.entity.ProductoCarrito;
import com.muebleria.entity.Usuario;
import com.muebleria.repository.FacturaDetalleRepository;
import com.muebleria.repository.FacturaRepository;
import com.muebleria.repository.UsuarioRepository;
import com.muebleria.repository.CarritoRepository;
import com.muebleria.service.CarritoService;
import com.muebleria.service.FacturaService;

import jakarta.transaction.Transactional;

@Service
public class FacturaServiceImpl implements FacturaService{

    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private FacturaDetalleRepository facturaDetalleRepository;
    
    @Autowired
    private CarritoService carritoService;
	
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
	@Transactional
	public Factura generarFactura(Long usuarioId) {
	    // Obtener el usuario desde la base de datos
	    Usuario usuario = usuarioRepository.findById(usuarioId)
	            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    // Obtener el carrito del usuario
	    Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);  // Pasamos el objeto Usuario

	    Factura factura = new Factura();
	    factura.setUsuario(carrito.getUsuario());
	    factura.setFechaEmision(LocalDateTime.now());
	    factura.setEstado("pendiente");

	    BigDecimal total = BigDecimal.ZERO;

	    for (ProductoCarrito productoCarrito : carrito.getProductos()) {
	        FacturaDetalle detalle = new FacturaDetalle();
	        detalle.setFactura(factura);
	        detalle.setMueble(productoCarrito.getMueble());
	        detalle.setCantidad(productoCarrito.getCantidad());

	        // Convertir precio (double) a BigDecimal
	        BigDecimal precio = BigDecimal.valueOf(productoCarrito.getPrecio());

	        detalle.setPrecio(precio);
	        // Multiplicar el precio por la cantidad
	        BigDecimal totalProducto = precio.multiply(BigDecimal.valueOf(productoCarrito.getCantidad()));

	        detalle.setTotal(totalProducto);
	        facturaDetalleRepository.save(detalle);

	        total = total.add(totalProducto);
	    }

	    factura.setTotal(total);
	    facturaRepository.save(factura);

	    // Marcar el carrito como cerrado
	    carrito.setActivo(false);
	    carritoRepository.save(carrito);

	    return factura;
	}


}
