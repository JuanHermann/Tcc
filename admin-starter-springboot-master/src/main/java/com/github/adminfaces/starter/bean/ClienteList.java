package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class ClienteList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public ClienteList() {
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
		setLista(usuarioRepository.findByAceitoOrderById(true));
	}
	
	public void aceitarSelecionados() {
		int num =0;
		for (int i = 0; i < getRegistrosSelecionados().size(); i++) {
			getRegistrosSelecionados().get(i).setAceito(true);
			usuarioRepository.save(getRegistrosSelecionados().get(i));
			num++;
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros aceitos com sucesso!");
		listar();
	}
	

}
