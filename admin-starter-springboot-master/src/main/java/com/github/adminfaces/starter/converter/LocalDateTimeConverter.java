package com.github.adminfaces.starter.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeConverter implements Converter {
	
	private DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Override
	public Object getAsObject(FacesContext context, 
			UIComponent component, String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			return LocalDateTime.parse(value, formatter);
		} catch (DateTimeParseException ex) {
			FacesMessage message = 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Data inválida", null);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, 
			UIComponent component, Object value) {
		if (value != null && value instanceof LocalDateTime) {
			LocalDateTime date = (LocalDateTime) value;
			return date.format(formatter);
		} else {
			return null;
		}
	}

}
