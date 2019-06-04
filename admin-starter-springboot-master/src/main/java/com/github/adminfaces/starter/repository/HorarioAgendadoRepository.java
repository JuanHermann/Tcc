package com.github.adminfaces.starter.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Usuario;


public interface HorarioAgendadoRepository extends JpaRepository<HorarioAgendado, Integer> {
	
	List<HorarioAgendado> findByDataOrderByHoraInicio(LocalDate data);
	
	List<HorarioAgendado> findByClienteAndDataGreaterThanEqualOrderByDataAsc(Usuario usuario,LocalDate data);
}
