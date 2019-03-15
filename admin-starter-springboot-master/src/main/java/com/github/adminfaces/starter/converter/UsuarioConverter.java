package com.github.adminfaces.starter.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;

@Component
public class UsuarioConverter implements Converter {

	@Autowired
	private UsuarioRepository repository;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		try {
			System.out.println("aqui");
			System.out.println(repository.findById(Integer.parseInt(value)).orElse(null).getNome());
			return repository.findById(Integer.parseInt(value)).orElse(null);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null && value instanceof Usuario) {
			Usuario usuario = (Usuario) value;
			return String.valueOf(usuario.getId());
		} else {
			return null;
		}
	}

}
