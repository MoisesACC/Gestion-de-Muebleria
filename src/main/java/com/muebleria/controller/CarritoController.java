package com.muebleria.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.muebleria.entity.Carrito;
import com.muebleria.entity.Factura;
import com.muebleria.entity.ProductoCarrito;
import com.muebleria.entity.Usuario;
import com.muebleria.repository.CarritoRepository;
import com.muebleria.repository.MuebleRepository;
import com.muebleria.repository.UsuarioRepository;
import com.muebleria.service.CarritoService;
import com.muebleria.service.FacturaService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MuebleRepository muebleRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;

    
    @PostMapping("/agregarProducto")
    @ResponseBody  // Esto asegura que el contenido se envíe como respuesta directa
    public String agregarProductoAlCarrito(@RequestParam Long productoId, 
                                           @RequestParam int cantidad, 
                                           Principal principal, 
                                           HttpSession session) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el username: " + username));

        carritoService.agregarProductoAlCarrito(usuario, productoId, cantidad);

        // Actualizar el carrito en la sesión
        Carrito carritoActualizado = carritoService.obtenerCarritoPorUsuario(usuario);
        session.setAttribute("carrito", carritoActualizado);

        // Retornar un mensaje de éxito
        return "Producto añadido al carrito";
    }
    

    // Método para obtener la cantidad de productos en el carrito del usuario
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> obtenerContadorCarrito(Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Obtener el número de productos en el carrito
        int count = carritoService.obtenerCantidadProductosEnCarrito(usuario.getId());

        // Crear el mapa con la cantidad de productos
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    // Método para comprar el carrito
    @PostMapping("/comprar")
    public String comprarCarrito(@RequestParam Long carritoId) {
        try {
            Factura factura = facturaService.generarFactura(carritoId);
            // Marcar el carrito como inactivo al realizar la compra
            Carrito carrito = carritoRepository.findById(carritoId)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
            carrito.setActivo(false);
            carritoRepository.save(carrito);

            return "redirect:/factura/" + factura.getId();  // Redirige a la página de la factura
        } catch (Exception e) {
            return "redirect:/carrito?error=true";  // Redirige en caso de error
        }
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Obtener el carrito del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
        double total = carritoService.calcularTotalCarrito(usuario);
        carrito.setTotal(total);

        session.setAttribute("carrito", carrito);

        model.addAttribute("carrito", carrito);
        model.addAttribute("totalProductos", carrito.getProductos().stream().mapToInt(ProductoCarrito::getCantidad).sum());
        return "carrito";
    }

    @PostMapping("/eliminarProducto")
    @ResponseBody
    public String eliminarProductoDelCarrito(@RequestParam Long productoId, Principal principal, HttpSession session) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el username: " + username));

        carritoService.eliminarProductoDelCarrito(usuario, productoId);

        // Actualizar el carrito en la sesión
        Carrito carritoActualizado = carritoService.obtenerCarritoPorUsuario(usuario);
        session.setAttribute("carrito", carritoActualizado);

        return "Producto eliminado del carrito";
    }
    @PostMapping("/actualizarCantidad")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarCantidadProducto(
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            Principal principal) {
        
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Si cantidad es positiva, agregamos al carrito
            // Si es negativa, la procesamos como una reducción
            carritoService.agregarProductoAlCarrito(usuario, productoId, cantidad);
            
            // Recalcular el total
            double total = carritoService.calcularTotalCarrito(usuario);
            
            response.put("success", true);
            response.put("message", "Cantidad actualizada correctamente");
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar la cantidad: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    

}

