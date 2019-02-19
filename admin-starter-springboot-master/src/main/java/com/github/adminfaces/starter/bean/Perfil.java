package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class Perfil extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;
	
	public Perfil() {
		super(Usuario.class);
	}
	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		setId(usuarioLogadoBean.getUsuario().getId());
		super.init();
	}
	
	public void atualizar() {		
		getRepository().save(getObjeto());		
		usuarioLogadoBean.setUsuario(getObjeto()); 
		addDetailMessage("Cadastro Atualizado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);		       
	}
	
	public void atualizarSenha() {		
		getRepository().save(getObjeto());		
		usuarioLogadoBean.setUsuario(getObjeto()); 
		addDetailMessage("Cadastro Atualizado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);		       
	}

	

   
}