package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.converter.TimeConverter;
import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


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
		Date data = new Date();
		try {
			data = hora.parse(inicio);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Time time = new Time(data.getTime());
	
		System.out.println(termino);
		System.out.println("--------");
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