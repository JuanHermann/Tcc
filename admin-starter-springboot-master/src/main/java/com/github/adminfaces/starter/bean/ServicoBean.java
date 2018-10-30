package com.github.adminfaces.starter.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;

@Component
@Scope("view")
public class ServicoBean extends AbastractBean<Servico, ServicoRepository>{
	
	
	public ServicoBean() {
		super(Servico.class);
	}
	

}
