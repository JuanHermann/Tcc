package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class UsuarioList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public UsuarioList() {
		super(Usuario.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(usuarioRepository.findByNomeLikeAndAceitoOrderById("%"+getNome()+"%",false));
		} else {
			listarNovos();
		}

	}	
	
	public void listarNovos() {
		setLista(usuarioRepository.findByAceitoOrderById(false));
	}
	
	@Override
	public void listar() {
		listarNovos();
	}
	

}
