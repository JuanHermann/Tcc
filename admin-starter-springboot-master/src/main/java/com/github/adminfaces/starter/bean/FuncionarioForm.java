package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import lombok.Getter;
import lombok.Setter;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Scope("view")
public class FuncionarioForm extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ServicoRepository servicoRepository;

	@Autowired
	private PermissaoRepository permissaoRepository;

	private List<Servico> lista;
	private String nome;

	private List<Servico> servicosSelecionados;

	private List<String> permissaoSelecionados;
	private List<String> permissoes;

	public FuncionarioForm() {
		super(Usuario.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		setId(Integer
				.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id")));
		super.init();
		lista = new ArrayList<>();

		permissaoSelecionados = permissaoToString(getObjeto().getPermissoes());
		permissoes = new ArrayList<>();
		permissoes.add("Funcionario");
		permissoes.add("Atendente");
		permissoes.add("Administrador");

		servicosSelecionados = new ArrayList<>();

		carregarLista();
		carregarServicos();
	}

	public void carregarLista() {
		setLista(servicoRepository.findByAtivoOrderByNome(true));

	}

	public void carregarServicos() {
		List<UsuarioServico> usuarioServicos = usuarioServicoRepository.findByUsuarioAndAtivo(getObjeto(), true);
		for (UsuarioServico usuarioServico : usuarioServicos) {
			servicosSelecionados.add(usuarioServico.getServico());
		}
	}

	@Override
	public void salvar() throws InstantiationException, IllegalAccessException {
		salvarServicos();
		salvarPermissoes();
		getRepository().save(getObjeto());
		addDetailMessage("Salvo com sucesso");
		servicosSelecionados.clear();
		carregarLista();
		carregarServicos();

	}

	private void salvarPermissoes() {
		List<Permissao> lista = stringToPermissao(permissaoSelecionados);
		boolean funcionario = Usuario.hasRole("ROLE_FUNCIONARIO", getObjeto());
		if (!getObjeto().getPermissoes().equals(lista)) {
			getObjeto().getPermissoes().clear();
			getObjeto().setPermissoes(lista);
			getRepository().save(getObjeto());
			if (!funcionario) {
				if (!permissaoSelecionados.contains("Funcionario")) {
					for (UsuarioServico us : usuarioServicoRepository.findByUsuarioAndAtivo(getObjeto(), true)) {
						us.setAtivo(false);
						usuarioServicoRepository.save(us);
					}
				} else {
					for (Servico servico : getLista()) {
						UsuarioServico us = usuarioServicoRepository.findByServicoAndUsuario(servico, getObjeto());
						us.setAtivo(true);
						usuarioServicoRepository.save(us);

					}
				}
			}

		}

	}

	private void salvarServicos() {
		List<Servico> listaAtualizada = servicosSelecionados;
		List<Servico> listaTodosServicosUsuario = new ArrayList<>();
		for (UsuarioServico us : usuarioServicoRepository.findByUsuario(getObjeto())) {
			listaTodosServicosUsuario.add(us.getServico());
		}
		for (Servico servico : listaTodosServicosUsuario) {
			UsuarioServico usuarioServico = usuarioServicoRepository.findByServicoAndUsuario(servico, getObjeto());
			if (listaAtualizada.contains(servico)) {
				if (!usuarioServico.isAtivo()) {
					usuarioServico.setAtivo(true);
					usuarioServicoRepository.save(usuarioServico);
				}
			} else {
				if (usuarioServico.isAtivo()) {
					usuarioServico.setAtivo(false);
					usuarioServicoRepository.save(usuarioServico);
				}
			}
		}

	}

	public void buscar() {
		if (getNome() != "") {
			setLista(servicoRepository.findByNomeLikeAndAtivoOrderByNome("%" + getNome() + "%", true));
		} else {
			setLista(servicoRepository.findByAtivoOrderByNome(true));
		}
		setNome("");

	}

	public List<String> permissaoToString(List<Permissao> lista) {
		List<String> retorno = new ArrayList<>();
		if (lista.contains(permissaoRepository.findByNome("ROLE_FUNCIONARIO"))) {
			retorno.add("Funcionario");
		}
		if (lista.contains(permissaoRepository.findByNome("ROLE_ATENDENTE"))) {
			retorno.add("Atendente");
		}
		if (lista.contains(permissaoRepository.findByNome("ROLE_ADMIN"))) {
			retorno.add("Administrador");
		}
		return retorno;

	}

	public List<Permissao> stringToPermissao(List<String> lista) {
		List<Permissao> retorno = new ArrayList<>();
		if (lista.contains("Funcionario")) {
			retorno.add(permissaoRepository.findByNome("ROLE_FUNCIONARIO"));
		}
		if (lista.contains("Atendente")) {
			retorno.add(permissaoRepository.findByNome("ROLE_ATENDENTE"));
		}
		if (lista.contains("Administrador")) {
			retorno.add(permissaoRepository.findByNome("ROLE_ADMIN"));
		}
		return retorno;

	}

	public boolean monstrarServicos() {
		return permissaoSelecionados.contains("Funcionario");
	}

}