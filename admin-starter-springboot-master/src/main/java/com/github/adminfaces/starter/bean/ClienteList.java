package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

@Component
@Scope("view")
public class ClienteList extends AbastractListBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	@Autowired
	private ServicoRepository servicoRepository;
	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;

	public ClienteList() {
		super(Usuario.class);
	}

	public void buscar() {

		if (getNome() != "") {
			setLista(Usuario.filtraPorClientes(
					usuarioRepository.findByNomeLikeAndAtivoOrderByNome("%" + getNome() + "%", true)));
		} else {
			listar();
		}
		setNome("");
		setRegistrosSelecionados(null);

	}

	public void tornarFuncionairo() {
		List<Usuario> usuarios = getRegistrosSelecionados();
		for (Usuario usuario : usuarios) {
			if (!usuario.getPermissoes().contains(permissaoRepository.findByNome("ROLE_FUNCIONARIO"))) {
				usuario.addPermissao(permissaoRepository.findByNome("ROLE_FUNCIONARIO"));
				getRepository().save(usuario);
				List<Servico> servicos = servicoRepository.findAll();
				for (Servico servico : servicos) {
					UsuarioServico us = new UsuarioServico();
					us.setServico(servico);
					us.setUsuario(usuario);
					us.setAtivo(true);
					usuarioServicoRepository.save(us);
				}
			}
		}
		addDetailMessage("Cliente atualizado para funcionario");
		listar();

	}

	@Override
	public void listar() {
		setLista(Usuario.filtraPorClientes(usuarioRepository.findByAtivoOrderByNome(true)));

	}

	@Override
	public void deletarSelecionados() {
		int num = 0;
		for (Usuario usuario : getRegistrosSelecionados()) {
			if (horarioAgendadoRepository.findByCliente(usuario).isEmpty()) {
				usuarioRepository.delete(usuario);
			} else {
				usuario.setAtivo(false);
				usuarioRepository.save(usuario);
				num++;
			}
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros deletados com sucesso!");
		listar();

	}
}
