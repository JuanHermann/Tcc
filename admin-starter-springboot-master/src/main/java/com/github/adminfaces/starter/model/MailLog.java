package com.github.adminfaces.starter.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MailLog {
	
	@Id
	@GeneratedValue
	private Integer codigo;
	@ManyToOne
	@JoinColumn(nullable=false)
	@JsonIgnore
	private MailConf conf;
	@Column(nullable=false, length=100)
	private String destinatario;
	private String assunto;
	@Column(nullable=false)
	private String mensagem;
	@Column(nullable=false)
	private LocalDateTime dataHoraEnvio;
	@Column(nullable=false)
	private Boolean enviado;

	public MailLog( MailConf conf, String destinatario, String assunto, String mensagem,
			LocalDateTime dataHoraEnvio, Boolean enviado) {
		super();
		this.conf = conf;
		this.destinatario = destinatario;
		this.assunto = assunto;
		this.mensagem = mensagem;
		this.dataHoraEnvio = dataHoraEnvio;
		this.enviado = enviado;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailLog other = (MailLog) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}



}
