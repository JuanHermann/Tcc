package com.github.adminfaces.starter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	List<Usuario> findByNomeLike(String nome);
	
	List<Usuario> findByNomeLikeAndAceitoOrderById(String nome,boolean aceito);
	
	List<Usuario> findByAceitoOrderById(boolean ativo);
}
