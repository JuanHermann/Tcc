package com.github.adminfaces.starter.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HorarioLivre {
	
	private String nome;
	private List<String> horarios;
	
	
	public HorarioLivre(String nome, List<String> horarios) {
		super();
		this.nome = nome;
		this.horarios = horarios;
	}
	
	
}
