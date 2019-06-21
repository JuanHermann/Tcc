package com.github.adminfaces.starter.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;


public interface UsuarioServicoRepository extends JpaRepository<UsuarioServico, Integer> {
	UsuarioServico findByServicoAndUsuario(Servico servico,Usuario usuario);
	List<UsuarioServico> findByUsuario(Usuario usuario);
	List<UsuarioServico> findByServicoOrderByUsuario(Servico servico);
	List<UsuarioServico> findByServicoAndAtivo(Servico servico,boolean ativo);
	List<UsuarioServico> findByUsuarioAndAtivo(Usuario usuario,boolean ativo);
	UsuarioServico findByServicoAndUsuarioAndAtivoOrderByUsuario(Servico servico,Usuario usuario,boolean ativo);
}
