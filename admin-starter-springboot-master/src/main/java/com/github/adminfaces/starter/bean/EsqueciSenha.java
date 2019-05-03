/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.MailService;
import com.github.adminfaces.starter.service.UsuarioService;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@Scope("view")
public class EsqueciSenha extends AbastractFormBean<Usuario, UsuarioRepository> {

	@Autowired
	private MailService service;
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired 
	private JavaMailSender mailSender;

	private String email;

	@Autowired
	private PermissaoRepository permissaoRepository;

	public EsqueciSenha() {
		super(Usuario.class);
	}

	public void novaSenha()  {
		Usuario usuario = usuarioRepository.findByEmail(email);
		if(usuario != null) {
			Random rand = new Random();
			String novaSenha = String.valueOf(Long.toHexString(rand.nextLong()));
			System.out.println(novaSenha);
			usuario.setSenha(novaSenha);
			usuarioService.criptografarSenha(usuario);
			getRepository().save(usuario);
			sendMail(usuario, novaSenha);
			setEmail("");
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Email não cadastrado em nosso sistema."));
		}
	}
    public void sendMail(Usuario usuario, String novaSenha) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("Olá "+ usuario.getNome()+",\n\nSua nova senha é "+novaSenha+" ,\nVocê pode alterar sua senha no seu perfil, localhots:8080/perfil.jsf\n\nAtenciosamente,\nJuan Hermann");
        message.setSubject("Recuração de Senha");
        message.setTo("juanhs8@hotmail.com");

        try {
            mailSender.send(message);
			addDetailMessage("Email enviado com sucesso!");
			Faces.getExternalContext().getFlash().setKeepMessages(true);
        } catch (Exception e) {
            e.printStackTrace();
			addDetailMessage("Falha ao enviar o Email!");
			Faces.getExternalContext().getFlash().setKeepMessages(true);
        }
    }

}