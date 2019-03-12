/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;
import java.util.List;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;
import com.github.adminfaces.starter.service.UsuarioService;

@Component
@Scope("view")
public class UsuarioForm extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private PermissaoRepository permissaoRepository;


	public UsuarioForm() {
		super(Usuario.class);
	}

	public void novoCadastro() throws IOException {
		System.out.println("novo cadastro");
		getObjeto().setAtivo(true);
		getObjeto().setAceito(false);
		usuarioService.criptografarSenha(getObjeto());
		getRepository().save(getObjeto());
		getObjeto().addPermissao(permissaoRepository.findByNome("ROLE_CADASTRADO"));
		getRepository().save(getObjeto());
		addDetailMessage("Cadastro criado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);
		Faces.redirect("index.jsf");
	}

	

}