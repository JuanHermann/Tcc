package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class FuncionarioServicoList extends AbastractListBean<UsuarioServico, UsuarioServicoRepository> {

	@Autowired
	private UsuarioServicoRepository servicoRepository;

	public FuncionarioServicoList() {
		super(UsuarioServico.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(servicoRepository.findByNomeLikeOrderById("%"+getNome()+"%"));
		} else {
			setLista(servicoRepository.findAll());
		}
		setNome("");
		setRegistrosSelecionados(null);

	}
	

	

}
