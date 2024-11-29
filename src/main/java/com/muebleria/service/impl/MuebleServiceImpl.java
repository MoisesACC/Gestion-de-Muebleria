package com.muebleria.service.impl;

import com.muebleria.entity.Mueble;
import com.muebleria.repository.MuebleRepository;
import com.muebleria.service.MuebleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MuebleServiceImpl implements MuebleService {

    @Autowired
    private MuebleRepository muebleRepository;

    @Override
    public List<Mueble> listarMuebles() {
        return muebleRepository.findAll();
    }

    @Override
    public Mueble guardarMueble(Mueble mueble) {
        return muebleRepository.save(mueble);
    }

    @Override
    public Mueble obtenerMueblePorId(Long id) {
        return muebleRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarMueble(Long id) {
        muebleRepository.deleteById(id);
    }
}
