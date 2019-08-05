package com.github.adminfaces.starter.bean;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;


@Component
@Scope("view")
public class BloquearAgendaForm extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {

	private String inicio,termino ;
	private SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");
	
	public BloquearAgendaForm() {
		super(HorarioAgendado.class);
	}
	@Override
	public void salvar() throws InstantiationException, IllegalAccessException {
		System.out.println("--------");
		System.out.println(data.format(inicio));
		System.out.println(hora.format(inicio));


	}
	public String getInicio() {
		return inicio;
	}
	public void setInicio(String inicio) {
		this.inicio = inicio;
	}
	public String getTermino() {
		return termino;
	}
	public void setTermino(String termino) {
		this.termino = termino;
	}

	
	
	
	
	
	
   
}