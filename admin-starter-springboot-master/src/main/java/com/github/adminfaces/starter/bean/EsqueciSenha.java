/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.MailConf;
import com.github.adminfaces.starter.model.MailLog;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.service.MailService;

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

	private String email;

	@Autowired
	private PermissaoRepository permissaoRepository;

	public EsqueciSenha() {
		super(Usuario.class);
	}

	public void novaSenha() throws IOException {
		Usuario usuario = usuarioRepository.findByEmail(email);
		if (usuario != null) {
			MailConf conf = new MailConf();
			conf.setEmail("juan.1998@alunos.utfpr.edu.br");
			conf.setNome("juan Hermann");
			conf.setPorta(8080);
			conf.setProtocolo("SMTP");
			conf.setSenha("ztrabu22");
			conf.setServidor("localhost");
			conf.setUsuario(usuario.getNome());
			MailLog mailLog = new MailLog(conf, usuario.getEmail(), "Recuperação de Senha",
					"Esta aqui sua nova senha", LocalDateTime.now(), false);
			service.enviar(mailLog, true);
			addDetailMessage("Enviado com sucesso");
		} else {
			addDetailMessage("Email não encontrado");
		}
	}

}