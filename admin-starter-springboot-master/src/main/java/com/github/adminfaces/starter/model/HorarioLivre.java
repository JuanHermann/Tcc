package com.github.adminfaces.starter.model;

import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HorarioLivre {
	
	private String nome;
	private List<LocalTime> horarios;
	
	
	public HorarioLivre(String nome, List<LocalTime> horarios) {
		super();
		this.nome = nome;
		this.horarios = horarios;
	}
	
	
}
