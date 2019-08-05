package com.github.adminfaces.starter.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.adminfaces.starter.model.Permissao;


public interface PermissaoRepository extends CrudRepository<Permissao, Long> {
	
	Permissao findByNome(String nome);


}
