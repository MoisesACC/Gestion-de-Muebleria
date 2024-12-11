package com.muebleria.controller;

import com.muebleria.entity.Mueble;
import com.muebleria.service.MuebleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
@Controller
@RequestMapping("/muebles")
public class MuebleController {

    @Autowired
    private MuebleService muebleService;
    
    @GetMapping
    public String listarMuebles(Model model) {
        List<Mueble> muebles = muebleService.listarMuebles();
        System.out.println("Lista de muebles: " + muebles.size());  // Esto te dirá cuántos muebles tienes
        model.addAttribute("muebles", muebles);
        return "muebles";
    }



    @GetMapping("/create-mueble")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("mueble", new Mueble());
        return "create-mueble";
    }

    @PostMapping("/save-mueble")
    public String guardarMueble(@RequestParam(value = "id", required = false) Long id,
                                 @RequestParam("nombre") String nombre,
                                 @RequestParam("descripcion") String descripcion,
                                 @RequestParam("precio") double precio,
                                 @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                 @RequestParam("imagenExistente") String imagenExistente) throws IOException {

        Mueble mueble;

        // Verificar si es una creación o actualización
        if (id != null && id > 0) {
            // Actualizar mueble existente
            mueble = muebleService.obtenerMueblePorId(id);
            if (mueble == null) {
                return "redirect:/muebles"; // Si no existe el mueble, redirige a la lista
            }
        } else {
            // Crear un nuevo mueble
            mueble = new Mueble();
        }

        // Actualizar los datos del mueble
        mueble.setNombre(nombre);
        mueble.setDescripcion(descripcion);
        mueble.setPrecio(precio);

        // Verificar si se subió una nueva imagen
        if (imagen != null && !imagen.isEmpty()) {
            // Crear carpeta de imágenes si no existe
            Path carpetaImagenes = Paths.get("src/main/resources/static/imagenes");
            if (!Files.exists(carpetaImagenes)) {
                Files.createDirectories(carpetaImagenes);
            }

            // Guardar la nueva imagen
            String imagenNombre = imagen.getOriginalFilename();
            Path ruta = carpetaImagenes.resolve(imagenNombre);
            Files.write(ruta, imagen.getBytes());

            mueble.setImagen("/imagenes/" + imagenNombre); // Actualizar imagen
        } else {
            // Mantener la imagen existente
            mueble.setImagen(imagenExistente);
        }

        // Guardar los cambios en la base de datos
        try {
            muebleService.guardarMueble(mueble);
        } catch (Exception e) {
            // Manejo de errores durante el guardado
            return "redirect:/muebles?error=save_failed"; // Redirigir con un mensaje de error
        }

        // Redirigir a la lista de muebles con éxito
        return "redirect:/muebles";
    }




    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Mueble mueble = muebleService.obtenerMueblePorId(id);
        if (mueble == null) {
            return "redirect:/muebles"; // Redirige si no se encuentra el mueble
        }
        model.addAttribute("mueble", mueble);
        return "edit-mueble";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMueble(@PathVariable Long id) {
        muebleService.eliminarMueble(id);
        return "redirect:/muebles";
    }
    
 // Método para mostrar los detalles del mueble
    @GetMapping("/detalles/{id}")
    public String mostrarDetallesMueble(@PathVariable Long id, Model model) {
        Mueble mueble = muebleService.obtenerMueblePorId(id);
        if (mueble == null) {
            return "redirect:/muebles"; // Redirige a la lista si no se encuentra el mueble
        }
        model.addAttribute("mueble", mueble);
        return "detalles";  // Esto devolverá el archivo 'detalles.html'
    }

 // Método para la página de inicio
    @GetMapping("/inicio")
    public String mostrarProductosDestacados(Model model) {
        // Obtener los productos destacados usando el servicio
        List<Mueble> productosDestacados = muebleService.obtenerProductosDestacados();
        model.addAttribute("productosDestacados", productosDestacados);
        return "inicio"; // Nombre del archivo HTML
    }
    
    @GetMapping("/nosotros")
    public String mostrarNosotros() {
        return "nosotros";  // Esto devolverá el archivo 'nosotros.html'
    }

    
    @GetMapping("/productos")
    public String filtrarProductos(
                                   @RequestParam(required = false) String precio,
                                   @RequestParam(required = false) String busqueda,
                                   Model model) {
        List<Mueble> muebles = muebleService.listarMuebles();



        // Filtrar por rango de precios
        if (precio != null && !precio.isEmpty()) {
            String[] rango = precio.split("-");
            double minPrecio = Double.parseDouble(rango[0]);
            double maxPrecio = rango.length > 1 ? Double.parseDouble(rango[1]) : Double.MAX_VALUE;
            muebles = muebles.stream()
                    .filter(m -> m.getPrecio() >= minPrecio && m.getPrecio() <= maxPrecio)
                    .collect(Collectors.toList());
        }

        // Filtrar por búsqueda
        if (busqueda != null && !busqueda.isEmpty()) {
            muebles = muebles.stream()
                    .filter(m -> m.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("muebles", muebles);
        return "productos";
    }
    @GetMapping("/contacto")
    public String mostrarContacto() {
        return "contacto";  // Esto devolverá el archivo 'contacto.html'
    }


}