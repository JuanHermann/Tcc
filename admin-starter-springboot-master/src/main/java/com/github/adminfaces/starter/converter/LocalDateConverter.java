package com.github.adminfaces.starter.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.stereotype.Component;

@Component
public class LocalDateConverter implements Converter {
	
	private DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public Object getAsObject(FacesContext context, 
			UIComponent component, String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			return LocalDate.parse(value, formatter);
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
		if (value != null && value instanceof LocalDate) {
			LocalDate date = (LocalDate) value;
			return date.format(formatter);
		} else {
			return null;
		}
	}

}
