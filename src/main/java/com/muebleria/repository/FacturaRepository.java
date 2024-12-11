package com.muebleria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.muebleria.entity.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long>{

}
