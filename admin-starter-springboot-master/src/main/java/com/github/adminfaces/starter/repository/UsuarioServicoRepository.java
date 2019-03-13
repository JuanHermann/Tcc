package com.github.adminfaces.starter.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;


public interface UsuarioServicoRepository extends JpaRepository<UsuarioServico, Integer> {
	List<UsuarioServico> findByServicoOrderByUsuario(Servico servico);
	UsuarioServico findByServico(Servico servico);
	UsuarioServico findByServicoAndUsuarioOrderByUsuario(Servico servico,Usuario usuario);
}
