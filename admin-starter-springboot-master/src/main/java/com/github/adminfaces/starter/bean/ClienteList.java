package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class ClienteList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	

	public ClienteList() {
		super(Usuario.class);
	}

	public void buscar() {
		
		if (getNome() != "") {
			buscarPorNomePermissao();
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	private void buscarPorNomePermissao() {
		boolean role = false;
		List<Usuario> usuarios = usuarioRepository.findByNomeLikeAndAceitoOrderById("%"+getNome()+"%",true);
		List<Usuario> pesquisa = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			List<Permissao> permissoes= usuario.getPermissoes();
			role = false;
			for (Permissao permissao : permissoes) {
				
				if(permissao.getNome().equals("ROLE_CLIENTE")) {
					role =true;
				}
			}
			if(role == true) {
				pesquisa.add(usuario);
			}
		}
		
		setLista(pesquisa);		
	}

	@Override
	public void listar() {
		Permissao permissao =  permissaoRepository.findByNome("ROLE_CLIENTE");
		setLista(permissao.getUsuarios()); 
		
	}
		

}
