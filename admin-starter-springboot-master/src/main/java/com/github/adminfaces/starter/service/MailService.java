package com.github.adminfaces.starter.service;

import java.time.LocalDateTime;
import java.util.List;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.adminfaces.starter.exception.Advertencia;
import com.github.adminfaces.starter.model.MailConf;
import com.github.adminfaces.starter.model.MailLog;
import com.github.adminfaces.starter.repository.MailConfRepository;
import com.github.adminfaces.starter.repository.MailLogRepository;



@Service
public class MailService {
	
	@Autowired
	private MailConfRepository confRepository;
	@Autowired
	private MailLogRepository logRepository;
	
	public void enviar(MailLog log, boolean novaMensagem) {
		MailConf conf = confRepository.findAll().stream()
				.findFirst().orElse(null);
		
		Email email = EmailBuilder.startingBlank()
				.from(conf.getNome(), conf.getEmail())
				.to(log.getDestinatario(), log.getDestinatario())
				.withSubject(log.getAssunto())
				.appendText(log.getMensagem())
				.buildEmail();
		
		TransportStrategy strategy;
		switch (conf.getProtocolo()) {
			case "SSL":
				strategy = TransportStrategy.SMTPS;
				break;
			case "TLS":
				strategy = TransportStrategy.SMTP_TLS;
				break;
			default:
				strategy = TransportStrategy.SMTP;
		}
		
		Mailer mailer = MailerBuilder
				.withSMTPServer(conf.getServidor(), conf.getPorta(),
						conf.getUsuario(), conf.getSenha())
				.withTransportStrategy(strategy)
				.buildMailer();
		
		if (novaMensagem) {
			// Gravar informações do e-mail a ser enviado
			log.setConf(conf);
			log.setDataHoraEnvio(LocalDateTime.now());
			log.setEnviado(false);
			logRepository.save(log);
		}
		
		// Enviar e-mail
		mailer.sendMail(email);
		
		// Se chegou aqui, o e-mail foi enviado corretamente,
		// dessa forma, atualizamos o registro no bd
		log.setEnviado(true);
		logRepository.save(log);
	}
	
	public List<MailLog> listarLog() {
		return logRepository.findAll();
	}
	
	public void reenviar(Integer codigo) throws Advertencia {
		MailLog log = logRepository.findById(codigo).orElse(null);
		if (log == null) {
			throw new Advertencia("Código inválido");
		}
		if (log.getEnviado()) {
			throw new Advertencia("E-mail já enviado");
		}
		enviar(log, false);
	}

}








