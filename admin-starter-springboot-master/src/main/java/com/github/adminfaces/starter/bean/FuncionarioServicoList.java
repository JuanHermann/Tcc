package com.github.adminfaces.starter.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

@Component
@Scope("view")
public class FuncionarioServicoList extends AbastractListBean<UsuarioServico, UsuarioServicoRepository> {

	@Autowired
	private UsuarioServicoRepository servicoRepository;

	public FuncionarioServicoList() {
		super(UsuarioServico.class);
	}

	public void buscar() {
		

	}
	

	

}
