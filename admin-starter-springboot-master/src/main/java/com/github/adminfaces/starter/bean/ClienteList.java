package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class ClienteList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	@Autowired
	private ServicoRepository servicoRepository;
	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;

	private Usuario usuario;

	public ClienteList() {
		super(Usuario.class);
	}

	public void buscar() {

		if (getNome() != "") {
			setLista(usuario.filtraPorClientes(usuarioRepository.findByNomeLikeAndAtivoOrderByNome("%" + getNome() + "%", true)));
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}


	public void tornarFuncionairo() {
		List<Usuario> usuarios = getRegistrosSelecionados();
		for (Usuario usuario : usuarios) {
			usuario.addPermissao(permissaoRepository.findByNome("ROLE_FUNCIONARIO"));
			getRepository().save(usuario);
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

	@Override
	public void listar() {
		usuario = new Usuario();
		setLista(usuario.filtraPorClientes(usuarioRepository.findByAtivoOrderByNome(true)));

	}

}
