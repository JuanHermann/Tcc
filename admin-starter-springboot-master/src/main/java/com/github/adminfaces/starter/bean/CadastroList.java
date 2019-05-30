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
public class CadastroList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	private Usuario usuario;
	
	public CadastroList() {
		super(Usuario.class);
	}
	
	

	public void buscarNovos() {
		if (getNome() != "") {
			
			setLista(usuario.filtraPorNovosCadastros(usuarioRepository.findByNomeLikeAndAtivoOrderByNome("%"+getNome()+"%",true)));
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	@Override
	public void listar() {
		usuario = new Usuario();
		setLista(usuario.filtraPorNovosCadastros(usuarioRepository.findByAtivoOrderByNome(true)));
		System.out.println();
		
	}


	public void aceitarSelecionados() {
		int num =0;
		for (Usuario usuario :getRegistrosSelecionados()) {
			usuario.removePermissao(permissaoRepository.findByNome("ROLECADASTRADO"));
			usuario.addPermissao(permissaoRepository.findByNome("ROLE_CLIENTE"));
			usuario.getPermissoes().remove(permissaoRepository.findByNome("ROLE_CADASTRADO"));
			usuarioRepository.save(usuario);
			num++;
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros aceitos com sucesso!");
		listar();
	}
	
	public void recusarSelecionados() {
		int num =0;
		for (int i = 0; i < getRegistrosSelecionados().size(); i++) {
			
			usuarioRepository.delete(getRegistrosSelecionados().get(i));
			
			num++;
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros Recusados com sucesso!");
		listar();
	}	

}
