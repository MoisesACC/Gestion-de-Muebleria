package com.muebleria.service.impl;

import com.muebleria.entity.Usuario;
import com.muebleria.repository.UsuarioRepository;
import com.muebleria.service.UsuarioService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        // Validar si el usuario ya existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso");
        }

        // Cifrar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Establecer un rol por defecto si no se proporciona
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        // Buscar usuario por username
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

	@Override
	public Usuario findById(Long usuarioId) {
	    // Buscar el usuario por id en el repositorio
	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
	    
	    // Si el usuario está presente, lo devolvemos; si no, podemos manejarlo de alguna manera
	    if (usuarioOpt.isPresent()) {
	        return usuarioOpt.get(); // Devolvemos el usuario si existe
	    } else {
	        // Podrías lanzar una excepción o devolver null, dependiendo de tu lógica de negocio
	        throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
	    }
	}
}
