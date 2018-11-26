package com.github.adminfaces.starter.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.UsuarioService;



@Service
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Integer> implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	protected JpaRepository<Usuario, Integer> getRepository() {
		return usuarioRepository;
	}

	@Override
	public List<Usuario> findByNomeLike(String nome) {
		return usuarioRepository.findByNomeLike(nome);
	}
	
	@Override
	public void criptografarSenha(Usuario usuario) throws RuntimeException {
		
	}


}
