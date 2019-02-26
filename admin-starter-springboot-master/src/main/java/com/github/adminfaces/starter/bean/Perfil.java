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
		
		String s = encoder.encode(senhaAtual);
		if(s.equals(getObjeto().getSenha())) {
			getObjeto().setSenha(novaSenha);
			usuarioService.criptografarSenha(getObjeto());
			getRepository().save(getObjeto());			
			usuarioLogadoBean.setUsuario(getObjeto()); 
			addDetailMessage("Senha Atualizada com sucesso!");
			Faces.getExternalContext().getFlash().setKeepMessages(true);		
			
		}else {
			System.out.println(s);
			System.out.println(getObjeto().getSenha());
			System.out.println("senhas não conferem");
		}
		
		       
	}
	
	
	public String getSenhaAtual() {
		return senhaAtual;
	}
	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}
	public String getNovaSenha() {
		return novaSenha;
	}
	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}
	
	

	

   
}