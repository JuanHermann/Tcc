package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class FuncionarioForm extends AbastractFormBean<Usuario, UsuarioRepository> {

	public FuncionarioForm() {
		super(Usuario.class);
	}
	
	

   
}