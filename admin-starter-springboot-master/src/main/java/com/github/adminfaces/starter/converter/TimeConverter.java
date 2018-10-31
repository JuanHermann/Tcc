package com.github.adminfaces.starter.converter;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.stereotype.Component;

@Component
public class TimeConverter implements Converter {

	private SimpleDateFormat formatador = new SimpleDateFormat("HH:mm:ss");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			Date data = formatador.parse(value);
			Time time = new Time(data.getTime());

			return time;
		} catch (DateTimeParseException | ParseException ex) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tempo(Time) inválido", null);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null && value instanceof LocalDate) {
			
			return "11:11:11";
		} else {
			return null;
		}
	}

	
}
