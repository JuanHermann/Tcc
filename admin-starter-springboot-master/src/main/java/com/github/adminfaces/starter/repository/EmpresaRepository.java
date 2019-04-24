package com.github.adminfaces.starter.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Empresa;



public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
		
}
