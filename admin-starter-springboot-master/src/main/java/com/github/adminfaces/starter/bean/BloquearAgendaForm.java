package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class BloquearAgendaForm extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {

	private Date inicio,termino ;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public BloquearAgendaForm() {
		super(HorarioAgendado.class);
	}
	@Override
	public void salvar() throws InstantiationException, IllegalAccessException {
		System.out.println("--------");
		System.out.println(simpleDateFormat.format(inicio));
		Time hora =  inicio.get;
		System.out.println(simpleDateFormat.format(inicio));
	
		System.out.println(termino);
		System.out.println("--------");
	}
	public Date getInicio() {
		return inicio;
	}
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}
	public Date getTermino() {
		return termino;
	}
	public void setTermino(Date termino) {
		this.termino = termino;
	}
	
	
	
	
	
	
   
}