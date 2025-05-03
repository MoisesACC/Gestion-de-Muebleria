package com.muebleria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.muebleria.entity.Carrito;
import com.muebleria.entity.Mueble;
import com.muebleria.entity.ProductoCarrito;

@Repository
public interface ProductoCarritoRepository  extends JpaRepository<ProductoCarrito, Long>{
	
    Optional<ProductoCarrito> findByCarritoAndMueble(Carrito carrito, Mueble mueble);

	void deleteAllByCarrito(Carrito carrito);
    

}
