package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class FuncionarioList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;

	public FuncionarioList() {
		super(Usuario.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(usuarioRepository.findByNomeLikeAndAceitoOrderById("%"+getNome()+"%",true));
			
			 
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	@Override
	public void listar() {
		Permissao permissao =  permissaoRepository.findByNome("ROLE_FUNCIONARIO");
		setLista(permissao.getUsuarios()); 
		
	}
		

}
