package com.muebleria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.muebleria.entity.ProductoCarrito;

@Repository
public interface ProductoCarritoRepository  extends JpaRepository<ProductoCarrito, Long>{

}
