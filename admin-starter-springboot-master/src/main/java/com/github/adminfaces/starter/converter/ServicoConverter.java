package com.github.adminfaces.starter.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;


@Component
public class ServicoConverter implements Converter {
	
	@Autowired
	private ServicoRepository repository;

	@Override
	public Object getAsObject(FacesContext context, 
			UIComponent component, String value) {
		try {
			return repository.findById(Integer.parseInt(value))
					.orElse(null);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, 
			UIComponent component, Object value) {
		if (value != null && value instanceof Servico) {
			Servico servico = (Servico) value;
			return String.valueOf(servico.getId());
		} else {
			return null;
		}
	}

}
