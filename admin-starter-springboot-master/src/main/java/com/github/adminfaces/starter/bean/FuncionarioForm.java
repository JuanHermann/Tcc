package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import lombok.Getter;
import lombok.Setter;

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
	private PermissaoRepository permissaoRepository;
	
	private List<Servico> servicosSelecionados;
	private List<String> permissaoSelecionados;	
	private List<String> permissoes;
	
	public FuncionarioForm() {
		super(Usuario.class);
	}
	
	@Override
	public void init() throws InstantiationException, IllegalAccessException {	
		permissoes = new ArrayList<>();
		permissoes.add("Funcionario");
		permissoes.add("Atendente");
		permissoes.add("Administrador");
		
		servicosSelecionados = new ArrayList<>();
		setId(Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id")));
		super.init();
		carregarServicos();
	}
	
	public void carregarServicos() {
		List<UsuarioServico> usuarioServicos = usuarioServicoRepository.findByUsuarioAndAtivo(getObjeto(), true);
		for(UsuarioServico usuarioServico : usuarioServicos) {
			servicosSelecionados.add(usuarioServico.getServico());
		}
	}
	
	@Override
	public void salvar() throws InstantiationException, IllegalAccessException {
		salvarServicos();
		super.salvar();
	}

	private void salvarServicos() {
		List<Servico> listaAtualizada = servicosSelecionados;
		carregarServicos();
		if(!listaAtualizada.equals(servicosSelecionados)) {
			for(Servico servico: listaAtualizada) {
				
			}
		}
		
	}
   
}