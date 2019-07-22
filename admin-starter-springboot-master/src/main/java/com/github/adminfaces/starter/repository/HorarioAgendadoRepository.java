package com.github.adminfaces.starter.repository;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Usuario;


public interface HorarioAgendadoRepository extends JpaRepository<HorarioAgendado, Integer> {
	
	@Query(value = "select h.id, h.data, h.hora_inicio, h.hora_termino, h.usuario_servico_id, h.usuario_id from horario_agendado h INNER JOIN usuario_servico u ON h.usuario_servico_id = u.id WHERE u.usuario_id = :id AND h.data = :data", nativeQuery = true)
	List<HorarioAgendado> findByFuncionarioAndData(@Param("id") Integer id,@Param("data")LocalDate data);
	
	@Query(value = "select h.id, h.data, h.hora_inicio, h.hora_termino, h.usuario_servico_id, h.usuario_id from horario_agendado h INNER JOIN usuario_servico u ON h.usuario_servico_id = u.id WHERE u.usuario_id = :id", nativeQuery = true)
	List<HorarioAgendado> findByFuncionario(@Param("id") Integer id);
	
	@Query(value = "select s.nome, COUNT (u.servico_id) as quantidade from horario_agendado h INNER JOIN usuario_servico u ON h.usuario_servico_id = u.id INNER JOIN servico s ON u.servico_id = s.id GROUP BY s.id order by quantidade desc", nativeQuery = true)
	List<HorarioAgendado> findRelatorioByServicoCount();
	
	List<HorarioAgendado> findByDataOrderByHoraInicio(LocalDate data);
	
	List<HorarioAgendado> findByClienteAndDataGreaterThanEqualOrderByDataAsc(Usuario usuario,LocalDate data);

	List<HorarioAgendado> findByCliente(Usuario usuario);
}
