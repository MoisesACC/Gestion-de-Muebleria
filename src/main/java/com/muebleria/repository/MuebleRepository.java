package com.muebleria.repository;

import com.muebleria.entity.Mueble;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuebleRepository extends JpaRepository<Mueble, Long> {
	 List<Mueble> findTop3ByOrderByPrecioDesc();
}
