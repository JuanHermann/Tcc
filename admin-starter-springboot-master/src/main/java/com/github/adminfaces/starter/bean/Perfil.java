package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.UsuarioService;

import lombok.Getter;
import lombok.Setter;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import javax.faces.application.FacesMessage;

import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
@Scope("view")
public class Perfil extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;
	@Autowired
	private UsuarioService usuarioService;
	
	private String senhaAtual,novaSenha;
	
	public Perfil() {
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
			Faces.getExternalContext().getFlash().setKeepMessages(true);	
			setNovaSenha("");
			setSenhaAtual("");
			fecharSenha();
			
		}else {
			addDetailMessage("Senha atual incorreta.",FacesMessage.SEVERITY_ERROR);
			Faces.getExternalContext().getFlash().setKeepMessages(true);	
		}		
		       
	}
	
	public void fecharSenha() {
		PrimeFaces.current().executeScript(String.format("document.getElementById('senhaSumir').style.display = 'none';"));
		PrimeFaces.current().executeScript(String.format("window.scrollTo(0, 0);"));
	}
	public void abrirSenha() {
		PrimeFaces.current().executeScript(String.format("document.getElementById('senhaSumir').style.display = 'block';"));
		PrimeFaces.current().executeScript(String.format("window.location.href='#foo';"));
	}
	

   
}