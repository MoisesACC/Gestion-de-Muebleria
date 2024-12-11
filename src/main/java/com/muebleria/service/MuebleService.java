package com.muebleria.service;

import com.muebleria.entity.Mueble;

import java.util.List;

public interface MuebleService {
    List<Mueble> listarMuebles();
    Mueble guardarMueble(Mueble mueble);
    Mueble obtenerMueblePorId(Long id);
    void eliminarMueble(Long id);
    List<Mueble> obtenerProductosDestacados();
}
