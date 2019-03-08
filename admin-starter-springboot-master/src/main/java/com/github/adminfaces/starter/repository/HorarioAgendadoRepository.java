package com.github.adminfaces.starter.repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Usuario;


public interface HorarioAgendadoRepository extends JpaRepository<HorarioAgendado, Integer> {

	List<HorarioAgendado> findByDataOrderByHoraInicio(Date data);
}
