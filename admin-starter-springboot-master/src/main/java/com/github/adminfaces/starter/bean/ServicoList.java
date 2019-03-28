package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class ServicoList extends AbastractListBean<Servico, ServicoRepository> {

	@Autowired
	private ServicoRepository servicoRepository;

	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	
	public ServicoList() {
		super(Servico.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(servicoRepository.findByNomeLikeAndAtivoOrderByNome("%" + getNome() + "%",true));
		} else {
			setLista(servicoRepository.findByAtivo(true));
		}
		setNome("");
		setRegistrosSelecionados(null);

	}
	
	@Override
	public void listar() {
		setLista(getRepository().findByAtivo(true));
	}

	@Override
	public void deletarSelecionados() {
		int num=0;
		for (Servico servico : getRegistrosSelecionados()) {

			servico.setAtivo(false);
			getRepository().save(servico);
			num++;
			for (UsuarioServico usuarioServico : usuarioServicoRepository.findByServicoAndAtivo(getObjeto(), true)) {
				usuarioServico.setAtivo(false);
				usuarioServicoRepository.save(usuarioServico);
			}
		}
		getRegistrosSelecionados().clear();
		addDetailMessage(num + " Registros deletado com sucesso!");
		listar();
	}

}
