package com.github.adminfaces.starter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	Optional<Usuario> findByEmail(String email);
		
	List<Usuario> findByNomeLikeAndAceitoOrderById(String nome,boolean aceito);
	
	List<Usuario> findByAceitoOrderById(boolean ativo);
}
