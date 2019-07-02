package com.github.adminfaces.starter.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Usuario;


public interface HorarioAgendadoRepository extends JpaRepository<HorarioAgendado, Integer> {
	
	@Query(value = "select h from horario_agendado h INNER JOIN usuario_servico u ON h.usuario_servico_id = u.id WHERE u.usuario_id = :id AND h.data = :data", nativeQuery = true)
	List<HorarioAgendado> findByFuncionarioAndData(@Param("id") Integer id,@Param("data")LocalDate data);
	
	List<HorarioAgendado> findByDataOrderByHoraInicio(LocalDate data);
	
	List<HorarioAgendado> findByClienteAndDataGreaterThanEqualOrderByDataAsc(Usuario usuario,LocalDate data);
}
