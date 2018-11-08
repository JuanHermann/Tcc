package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class ServicoList extends AbastractListBean<Servico, ServicoRepository> {

	public ServicoList() {
		super(Servico.class);
	}

   
}
