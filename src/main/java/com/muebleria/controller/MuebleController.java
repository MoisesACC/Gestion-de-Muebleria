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
import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping("/muebles")
public class MuebleController {

    @Autowired
    private MuebleService muebleService;

    @GetMapping
    public String listarMuebles(Model model) {
        model.addAttribute("muebles", muebleService.listarMuebles());
        return "muebles";
    }

    @GetMapping("/create-mueble")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("mueble", new Mueble());
        return "create-mueble";
    }

    @PostMapping("/save-mueble")
    public String guardarMueble(@RequestParam("nombre") String nombre,
                                @RequestParam("descripcion") String descripcion,
                                @RequestParam("precio") double precio,
                                @RequestParam("imagen") MultipartFile imagen) throws IOException {

        // Guardar la imagen en una carpeta local
        String imagenNombre = imagen.getOriginalFilename();
        Path ruta = Paths.get("src/main/resources/static/imagenes/" + imagenNombre); // La carpeta 'imagenes' debe existir
        Files.write(ruta, imagen.getBytes()); // Guardar la imagen

        // Crear el mueble y asignar la ruta de la imagen
        Mueble mueble = new Mueble();
        mueble.setNombre(nombre);
        mueble.setDescripcion(descripcion);
        mueble.setPrecio(precio);
        mueble.setImagen("/imagenes/" + imagenNombre); // Ruta de la imagen en el servidor

        muebleService.guardarMueble(mueble); // Guardar el mueble
        return "redirect:/muebles"; // Redirige a la lista de muebles después de guardar
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

    @PostMapping("/editar/{id}")
    public String actualizarMueble(@PathVariable Long id, @RequestParam("nombre") String nombre,
                                    @RequestParam("descripcion") String descripcion,
                                    @RequestParam("precio") double precio,
                                    @RequestParam("imagen") MultipartFile imagen) throws IOException {

        Mueble muebleExistente = muebleService.obtenerMueblePorId(id);
        if (muebleExistente == null) {
            return "redirect:/muebles"; // Redirige si no se encuentra el mueble
        }

        // Actualizar los campos del mueble existente
        muebleExistente.setNombre(nombre);
        muebleExistente.setDescripcion(descripcion);
        muebleExistente.setPrecio(precio);

        // Si se ha cargado una nueva imagen, guardarla
        if (!imagen.isEmpty()) {
            String imagenNombre = imagen.getOriginalFilename();
            Path ruta = Paths.get("src/main/resources/static/imagenes/" + imagenNombre);
            Files.write(ruta, imagen.getBytes());
            muebleExistente.setImagen("/imagenes/" + imagenNombre); // Ruta de la imagen en el servidor
        }

        muebleService.guardarMueble(muebleExistente); // Guardar el mueble actualizado
        return "redirect:/muebles"; // Redirige a la lista de muebles después de guardar
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMueble(@PathVariable Long id) {
        muebleService.eliminarMueble(id);
        return "redirect:/muebles";
    }

    // Método para la página de inicio
    @GetMapping("/inicio")
    public String mostrarPaginaInicio() {
        return "inicio";  // Esto devolverá el archivo 'inicio.html'
    }
}
