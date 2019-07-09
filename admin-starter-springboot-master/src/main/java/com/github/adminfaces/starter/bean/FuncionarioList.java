package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

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
	private UsuarioServicoRepository usuarioServicoRepository;

	private boolean role;

	public FuncionarioList() {
		super(Usuario.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(filtrarFuncionarios(
					usuarioRepository.findByNomeLikeAndAtivoOrderByNome("%" + getNome() + "%", true)));

		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}

	@Override
	public void listar() {
		setLista(filtrarFuncionarios(usuarioRepository.findByAtivoOrderByNome(true)));

	}

	private List<Usuario> filtrarFuncionarios(List<Usuario> lista) {
		List<Usuario> usuarios = lista;
		List<Usuario> pesquisa = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			List<Permissao> permissoes = usuario.getPermissoes();
			role = false;
			for (Permissao p : permissoes) {
				if (p.getNome().equals("ROLE_ATENDENTE")) {
					role = true;
				} else if (p.getNome().equals("ROLE_FUNCIONARIO")) {
					role = true;
				}
			}
			if (role == true) {
				pesquisa.add(usuario);
			}
		}
		return pesquisa;
	}

	@Override
	public void deletarSelecionados() {
		int num = 0;
		for (Usuario funcionario : getRegistrosSelecionados()) {
			funcionario.setAtivo(false);
			funcionario.getPermissoes().clear();
			getRepository().save(getObjeto());
				for (UsuarioServico us : usuarioServicoRepository.findByUsuarioAndAtivo(funcionario, true)) {
					us.setAtivo(false);
					usuarioServicoRepository.save(us);
				}

		num++;	
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros deletado com sucesso!");
		listar();
	}

}
