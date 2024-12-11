package com.muebleria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.muebleria.entity.FacturaDetalle;

@Repository
public interface FacturaDetalleRepository extends JpaRepository<FacturaDetalle, Long>{

}
