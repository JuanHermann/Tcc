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

	public CadastroList() {
		super(Usuario.class);
	}

	public void buscarNovos() {
		if (getNome() != "") {
			buscarPorNomePermissao();
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}	
		
	@Override
	public void listar() {
		Permissao permissao =  permissaoRepository.findByNome("ROLE_CADASTRADO");
		setLista(permissao.getUsuarios()); 
	}
	
	public void aceitarSelecionados() {
		int num =0;
		for (int i = 0; i < getRegistrosSelecionados().size(); i++) {
			getRegistrosSelecionados().get(i).setAceito(true);
			List<Permissao> p =  getRegistrosSelecionados().get(i).getPermissoes();
			permissaoRepository.delete(p.get(0));
			getRegistrosSelecionados().get(i).addPermissao(permissaoRepository.findByNome("ROLE_CLIENTE"));
			usuarioRepository.save(getRegistrosSelecionados().get(i));
			//errorrr
			num++;
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros aceitos com sucesso!");
		listar();
	}
	
	private void buscarPorNomePermissao() {
		boolean role = false;
		List<Usuario> usuarios = usuarioRepository.findByNomeLikeAndAceitoOrderById("%"+getNome()+"%",true);
		List<Usuario> pesquisa = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			List<Permissao> permissoes= usuario.getPermissoes();
			role = false;
			for (Permissao permissao : permissoes) {
				
				if(permissao.getNome().equals("ROLE_CADASTRADO")) {
					role =true;
				}
			}
			if(role == true) {
				pesquisa.add(usuario);
			}
		}
		
		setLista(pesquisa);		
	}
	

}
