package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class ServicoList extends AbastractListBean<Servico, ServicoRepository> {

	@Autowired
	private ServicoRepository servicoRepository;

	public ServicoList() {
		super(Servico.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(servicoRepository.findByNomeLikeOrderById("%"+getNome()+"%"));
		} else {
			setLista(servicoRepository.findAll());
		}

	}

	

}
