/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.UsuarioService;


@Component
@Scope("view")
public class Senha extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	public Senha() {
		super(Usuario.class);
	}
	
	public void novoCadastro() throws IOException {
		getObjeto().setAtivo(true);
		getObjeto().setAceito(false);
		getObjeto().addPermissao(permissaoRepository.findByNome("ROLE_USER"));
		usuarioService.criptografarSenha(getObjeto());
		getRepository().save(getObjeto());		
		addDetailMessage("Cadastro criado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);
        Faces.redirect("index.jsf");
	}

   
}