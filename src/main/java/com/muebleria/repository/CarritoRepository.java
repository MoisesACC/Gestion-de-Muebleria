package com.muebleria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Usuario;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioAndActivo(Usuario usuario, boolean activo);

	Carrito findByUsuario(Usuario usuario);

}
