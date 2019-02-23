package com.github.adminfaces.starter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	
	Optional<Usuario> findByEmailAndAtivo(String email,boolean ativo);
		
	List<Usuario> findByNomeLikeAndAtivoOrderByNome(String nome,boolean ativo);
	
	List<Usuario> findByAtivoOrderByNome(boolean ativo);
	
}
