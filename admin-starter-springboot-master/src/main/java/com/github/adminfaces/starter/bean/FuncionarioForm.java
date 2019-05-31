package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
	
	private List<Servico> servicosSelecionados;
	
	public FuncionarioForm() {
		super(Usuario.class);
	}
	
	@Override
	public void init() throws InstantiationException, IllegalAccessException {		
		super.init();
		//carregarServicos();
	}
	
	public void carregarServicos() {
		List<UsuarioServico> usuarioServicos = usuarioServicoRepository.findByUsuarioAndAtivo(getObjeto(), true);
		for(UsuarioServico usuarioServico : usuarioServicos) {
			servicosSelecionados.add(usuarioServico.getServico());
		}
	}
   
}