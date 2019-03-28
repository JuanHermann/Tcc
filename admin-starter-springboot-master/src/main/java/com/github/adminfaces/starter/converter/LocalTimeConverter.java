package com.github.adminfaces.starter.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
public class LocalTimeConverter implements Converter {

	private SimpleDateFormat formatador = new SimpleDateFormat("HH:mm:ss");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			LocalTime time =  LocalTime.parse(value);
			

			return time;
		} catch (DateTimeParseException ex) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tempo(LocalTime) inválido", null);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null && value instanceof LocalTime) {
			LocalTime time = (LocalTime) value;
			return time.toString();
		} else {
			return null;
		}
	}

	
}
