package com.muebleria.repository;

import com.muebleria.entity.Mueble;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuebleRepository extends JpaRepository<Mueble, Long> {
}
