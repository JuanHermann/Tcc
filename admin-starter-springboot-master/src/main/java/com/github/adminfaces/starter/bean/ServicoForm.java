package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import lombok.Getter;
import lombok.Setter;


@Component
@Getter
@Setter
@Scope("view")
public class ServicoForm extends AbastractFormBean<Servico, ServicoRepository> {
	
	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	private UsuarioServico usuarioServico;
	private Usuario cliente;
	
	@Autowired
	private PermissaoRepository permissaoRepository;

	public ServicoForm() {
		super(Servico.class);
	}
	
	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		setId(Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id")));
		super.init();
	}
	
	@Override
	public void salvar() throws InstantiationException, IllegalAccessException {
		getObjeto().setAtivo(true);
		getRepository().save(getObjeto());
		salvarUsuarioServico();
		addDetailMessage("Salvo com sucesso");
	}

	private void salvarUsuarioServico() {
		List<Usuario> usuarios = permissaoRepository.findByNome("ROLE_FUNCIONARIO").getUsuarios();				
		for(Usuario usuario: usuarios) {
			usuarioServico = new UsuarioServico();
			usuarioServico.setUsuario(usuario);
			usuarioServico.setServico(getObjeto());
			usuarioServico.setAtivo(true);
			usuarioServicoRepository.save(usuarioServico);
		}		
	}
	
	
	@Override
	public void remover() throws InstantiationException, IllegalAccessException {
		getObjeto().setAtivo(false);
		getRepository().save(getObjeto());
		for(UsuarioServico usuarioServico :  usuarioServicoRepository.findByServicoAndAtivo(getObjeto(),true)) {
			usuarioServico.setAtivo(false);
			usuarioServicoRepository.save(usuarioServico);
		}
	}
	
	
	
   
}