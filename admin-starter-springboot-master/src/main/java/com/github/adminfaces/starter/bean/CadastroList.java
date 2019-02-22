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

	private boolean role = false;
	
	public CadastroList() {
		super(Usuario.class);
	}

	public void buscarNovos() {
		if (getNome() != "") {
			setLista(retirarCadastrosCliente(usuarioRepository.findByNomeLikeOrderByNome("%"+getNome()+"%")));
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	@Override
	public void listar() {
		Permissao permissao =  permissaoRepository.findByNome("ROLE_CADASTRADO");
		setLista(retirarCadastrosCliente(permissao.getUsuarios()));
		
	}
	
	private List<Usuario> retirarCadastrosCliente(List<Usuario> lista) {
		List<Usuario> usuarios = lista;
		List<Usuario> pesquisa  = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			List<Permissao> permissoes= usuario.getPermissoes();
			role = false;
			for (Permissao p : permissoes) {
				
				if(p.getNome().equals("ROLE_CLIENTE")) {
					role =true;
				}
			}
			if(role == false) {
				pesquisa.add(usuario);
			}
		}
		
		return pesquisa;		
	}

	public void aceitarSelecionados() {
		int num =0;
		for (int i = 0; i < getRegistrosSelecionados().size(); i++) {
			getRegistrosSelecionados().get(i).addPermissao(permissaoRepository.findByNome("ROLE_CLIENTE"));
			usuarioRepository.save(getRegistrosSelecionados().get(i));
			//errorrr
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
