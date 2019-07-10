/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.UsuarioService;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@Scope("view")
public class UsuarioForm extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private PermissaoRepository permissaoRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;

	private String senha2;

	public UsuarioForm() {
		super(Usuario.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		senha2 = "";
		super.init();
	}

	public void novoCadastro() throws IOException {
		if (usuarioRepository.findByEmail(getObjeto().getEmail()) == null) {
			getObjeto().setAtivo(true);
			getObjeto().setAceito(false);
			usuarioService.criptografarSenha(getObjeto());
			getRepository().save(getObjeto());
			getObjeto().addPermissao(permissaoRepository.findByNome("ROLE_CADASTRADO"));
			getRepository().save(getObjeto());
			Faces.getExternalContext().getFlash().setKeepMessages(true);
			addDetailMessage("Cadastro criado com sucesso!");
			Faces.redirect("index.jsf");
		} else {
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Email já cadastrado"));
		}
	}

}