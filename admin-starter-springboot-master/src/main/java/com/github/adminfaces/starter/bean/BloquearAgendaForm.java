package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;

import org.primefaces.component.calendar.Calendar;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class BloquearAgendaForm extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {

	private Calendar inicio,termino = new Calendar();
	
	public BloquearAgendaForm() {
		super(HorarioAgendado.class);
	}
	@Override
	public void salvar() {
		//getObjeto().setData(inicio.getTime());
		System.out.println(inicio);
		System.out.println(termino);
	}
	
   
}