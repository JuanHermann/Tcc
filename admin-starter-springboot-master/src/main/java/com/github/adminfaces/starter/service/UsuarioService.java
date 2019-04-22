package com.github.adminfaces.starter.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

@Service
public class UsuarioService implements UserDetailsService, CommandLineRunner {

	@Autowired
	private UsuarioRepository repository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	@Autowired
	private ServicoRepository servicoRepository;
	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return repository.findByEmailAndAtivo(email,true).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
	}

	public void criptografarSenha(Usuario usuario) throws RuntimeException {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (usuario.getId() == null) {
			usuario.setSenha(encoder.encode(usuario.getSenha()));
		} else {
			Usuario antigo = repository.findById(usuario.getId()).orElse(null);
			if (antigo != null && !usuario.getSenha().equals(antigo.getSenha())) {
				usuario.setSenha(encoder.encode(usuario.getSenha()));
			}
		}
	}

	@Override
	public void run(String... args) throws Exception {
		Usuario usuario = repository.findByEmailAndAtivo(Usuario.ADMIN_EMAIL,true).orElse(null);
		if (usuario == null) {
			usuario = new Usuario();
			usuario.setEmail("admin@admin.com");
			usuario.setSenha("123");
			usuario.setNome("Administrador");
			usuario.setTelefone("(99)99999-9999");
			usuario.setAceito(true);
			usuario.setAtivo(true);
			criptografarSenha(usuario);
			repository.save(usuario);
			usuario.addPermissao(permissaoRepository.findByNome("ROLE_ADMIN"));
			usuario.addPermissao(permissaoRepository.findByNome("ROLE_FUNCIONARIO"));
			repository.save(usuario);			
			List<Servico> servicos = servicoRepository.findAll();
			for (Servico servico : servicos) {
				UsuarioServico us = new UsuarioServico();
				us.setServico(servico);
				us.setUsuario(usuario);
				us.setAtivo(true);
				usuarioServicoRepository.save(us);
			}
		}
	}

}
