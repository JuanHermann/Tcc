package com.github.adminfaces.starter.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.UsuarioService;



@Service
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Integer> implements UsuarioService,UserDetailsService, CommandLineRunner {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	protected JpaRepository<Usuario, Integer> getRepository() {
		return usuarioRepository;
	}
	
	
	@Override
	public void criptografarSenha(Usuario usuario) throws RuntimeException {
		
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return usuarioRepository.findByEmail(email).orElseThrow(() ->
		new UsernameNotFoundException("Usuário não encontrado"));
	}


}
