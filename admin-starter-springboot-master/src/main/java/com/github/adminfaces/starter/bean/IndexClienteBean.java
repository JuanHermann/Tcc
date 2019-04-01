package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.UsuarioService;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@Scope("view")
public class IndexClienteBean extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;
	@Autowired
	private UsuarioService usuarioService;
	
	private String senhaAtual,novaSenha;
	
	public IndexClienteBean() {
		super(Usuario.class);
	}
	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		setId(usuarioLogadoBean.getUsuario().getId());
		super.init();
	}
	
	public void atualizar() {		
		getRepository().save(getObjeto());		
		usuarioLogadoBean.setUsuario(getObjeto()); 
		addDetailMessage("Cadastro Atualizado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);		       
	}
	
	public void atualizarSenha() {	
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if(encoder.matches(senhaAtual, getObjeto().getSenha())){
			getObjeto().setSenha(novaSenha);
			usuarioService.criptografarSenha(getObjeto());
			getRepository().save(getObjeto());			
			usuarioLogadoBean.setUsuario(getObjeto()); 
			addDetailMessage("Senha Atualizada com sucesso!");
			System.out.println("senha alterada");
			Faces.getExternalContext().getFlash().setKeepMessages(true);		
			
		}else {
			System.out.println("senhas não conferem");
		}
		
		       
	}


	

   
}