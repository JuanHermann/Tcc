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
	
	private boolean role;
	
	

	public ClienteList() {
		super(Usuario.class);
	}

	public void buscar() {
		
		if (getNome() != "") {
			setLista(filtrarCliente(usuarioRepository.findByNomeLikeAndAtivoOrderByNome("%"+getNome()+"%",true)));
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	private List<Usuario> filtrarCliente(List<Usuario> lista) {
		List<Usuario> usuarios = lista;
		List<Usuario> pesquisa  = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			List<Permissao> permissoes= usuario.getPermissoes();
			role = false;
			for (Permissao p : permissoes) {
				if(p.getNome().equals("ROLE_ATENDENTE")) {
					role =false;
					break;
				}else if(p.getNome().equals("ROLE_FUNCIONARIO")) {
					role =false;
					break;
				}else if(p.getNome().equals("ROLE_CLIENTE")) {
					role =true;
					break;
				}
			}
			if(role == true) {
				pesquisa.add(usuario);
			}
		}
		
		return pesquisa;		
	}

	@Override
	public void listar() {
		Permissao permissao =  permissaoRepository.findByNome("ROLE_CLIENTE");
		setLista(filtrarCliente(permissao.getUsuarios()));
		
	}
		

}
