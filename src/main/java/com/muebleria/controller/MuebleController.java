package com.muebleria.controller;

import com.muebleria.entity.Mueble;
import com.muebleria.service.MuebleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("mueble", new Mueble());
        return "crear-mueble";
    }

    @PostMapping
    public String guardarMueble(Mueble mueble) {
        muebleService.guardarMueble(mueble);
        return "redirect:/muebles";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("mueble", muebleService.obtenerMueblePorId(id));
        return "crear-mueble";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMueble(@PathVariable Long id) {
        muebleService.eliminarMueble(id);
        return "redirect:/muebles";
    }
}
