package com.github.adminfaces.starter.bean;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Usuario;


@Component
@Scope("session")
public class UsuarioLogadoBean {
	
	private Usuario usuario;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@PostConstruct
	public void iniciar() {
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			usuario = (Usuario) authentication.getPrincipal();
		}
	}

}




