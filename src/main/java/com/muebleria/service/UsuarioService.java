package com.muebleria.service;

import com.muebleria.entity.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(Usuario usuario);
    Usuario buscarPorUsername(String username);
	Usuario findById(Long usuarioId);
}
