package com.github.adminfaces.starter.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.HorarioAgendado;


public interface HorarioAgendadoRepository extends JpaRepository<HorarioAgendado, Integer> {

	List<HorarioAgendado> findByDataOrderByHoraInicio(Date data);
}
