package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class ServicoForm extends AbastractFormBean<Servico, ServicoRepository> {
	
	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	private UsuarioServico usuarioServico;
	
	@Autowired
	private PermissaoRepository permissaoRepository;

	public ServicoForm() {
		super(Servico.class);
	}
	
	@Override
	public void salvar() throws InstantiationException, IllegalAccessException {
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
			usuarioServicoRepository.save(usuarioServico);
		}
		
		
	}
	
   
}