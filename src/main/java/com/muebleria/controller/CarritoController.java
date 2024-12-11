package com.muebleria.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Factura;
import com.muebleria.entity.Mueble;
import com.muebleria.entity.ProductoCarrito;
import com.muebleria.entity.Usuario;
import com.muebleria.repository.CarritoRepository;
import com.muebleria.repository.MuebleRepository;
import com.muebleria.repository.UsuarioRepository;
import com.muebleria.service.CarritoService;
import com.muebleria.service.FacturaService;
import com.muebleria.service.MuebleService;
import com.muebleria.service.impl.MuebleServiceImpl;

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
    public String agregarProductoAlCarrito(@RequestParam Long productoId, @RequestParam int cantidad, Principal principal) {
        // Obtener el nombre de usuario de la sesión actual
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el username: " + username));

        // Obtener el carrito del usuario
        Long usuarioId = usuario.getId(); // Obtener el ID del usuario
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario); // Asegúrate de que esta función devuelva el carrito

        // Si no hay carrito, crea uno nuevo y lo guarda
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carritoService.guardarCarrito(carrito);  // Guarda el carrito nuevo
        }

        // Delegar la tarea de agregar el producto al carrito al servicio
        carritoService.agregarProductoAlCarrito(carrito.getId(), productoId, cantidad);

        return "redirect:/carrito";  // Redirigir a la vista del carrito
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
            return "redirect:/factura/" + factura.getId();  // Redirige a la página de la factura
        } catch (Exception e) {
            return "redirect:/carrito?error=true";  // Redirige en caso de error
        }
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el username: " + username));

        Carrito carrito = (Carrito) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = carritoService.obtenerCarritoPorUsuario(usuario);
            session.setAttribute("carrito", carrito);  // Guardar en la sesión
        }

        int totalProductos = carrito.getProductos().stream().mapToInt(ProductoCarrito::getCantidad).sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("totalProductos", totalProductos);
        return "carrito";
    }


}

