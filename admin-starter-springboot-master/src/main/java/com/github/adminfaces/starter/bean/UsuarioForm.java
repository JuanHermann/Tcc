/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.service.UsuarioService;
import com.github.adminfaces.starter.service.impl.UsuarioServiceImpl;

import org.omnifaces.util.Faces;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class UsuarioForm implements Serializable {

	private Long id;
	private Usuario usuario;

	@Inject
	UsuarioServiceImpl usuarioService;

	public void init() {
		if (Faces.isAjaxRequest()) {
			return;
		}
		if (has(id)) {
			usuario = usuarioService.findOne(id);
		} else {
			usuario = new Usuario();
		}
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void remove() throws IOException {
		if (has(usuario) && has(usuario.getId())) {
			usuarioService.delete(usuario);
			addDetailMessage("Usuario " + usuario.getNome() + " removed successfully");
			Faces.getFlash().setKeepMessages(true);
			Faces.redirect("usuario-list.jsf");
		}
	}

	public void save() {
		String msg;
		usuarioService.save(usuario);
		msg = "Usuario " + usuario.getNome() + " created successfully";

		addDetailMessage(msg);
	}

	public void clear() {
		usuario = new Usuario();
		id = null;
	}

	public boolean isNew() {
		return usuario == null || usuario.getId() == null;
	}

}
