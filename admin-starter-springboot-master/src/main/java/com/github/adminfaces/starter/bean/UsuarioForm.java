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

	public UsuarioForm() {
		super(Usuario.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		super.init();
	}

public void novoCadastro() throws IOException {
	if (usuarioRepository.findByEmail(getObjeto().getEmail()) == null) {
		getObjeto().setAtivo(true);
		usuarioService.criptografarSenha(getObjeto());
		getRepository().save(getObjeto());
		getObjeto().addPermissao(permissaoRepository.findByNome("ROLE_CADASTRADO"));
		getRepository().save(getObjeto());
		addDetailMessage("Cadastro criado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);
		Faces.redirect("index.jsf");
	} else {
		addDetailMessage("Email já cadastrado");
	}
}

}