package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class DesativadoList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	public DesativadoList() {
		super(Usuario.class);
	}
	
	

	public void buscarNovos() {
		if (getNome() != "") {
			
			setLista(usuarioRepository.findByNomeLikeAndAtivoOrderByNome("%"+getNome()+"%",false));
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	@Override
	public void listar() {
		setLista(usuarioRepository.findByAtivoOrderByNome(false));
		System.out.println();
		
	}


	public void ativarSelecionados() {
		int num =0;
		for (Usuario usuario :getRegistrosSelecionados()) {
			usuario.getPermissoes().clear();
			usuario.addPermissao(permissaoRepository.findByNome("ROLE_CLIENTE"));
			usuario.setAtivo(true);
			usuarioRepository.save(usuario);
			num++;
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros reativados com sucesso!");
		listar();
	}


}
