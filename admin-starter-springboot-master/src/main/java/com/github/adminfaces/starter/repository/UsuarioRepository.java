package com.github.adminfaces.starter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	

	Optional<Usuario> findByEmailAndAceito(String email,boolean aceito);
	
	Optional<Usuario> findByEmailAndAtivo(String email,boolean ativo);
		
	List<Usuario> findByNomeLikeOrderByNome(String nome);
	
	List<Usuario> findByAceitoOrderById(boolean ativo);
	
}
