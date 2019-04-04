package com.github.adminfaces.starter.bean;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

@Component
public class ContextBean {
	
	public void abrirDialog(final String id) {
		PrimeFaces.current().executeScript(
				String.format("PF('%s').show();", id));
	}
	
	private void addMensagem(FacesMessage.Severity tipo, 
			String mensagem) {
		FacesMessage message = new FacesMessage(tipo, mensagem, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addMensagemErro(String mensagem) {
		addMensagem(FacesMessage.SEVERITY_ERROR, mensagem);
	}
	
	public void addMensagemInfo(String mensagem) {
		addMensagem(FacesMessage.SEVERITY_INFO, mensagem);
	}

	public void fecharDialog(final String id) {
		PrimeFaces.current().executeScript(
				String.format("PF('%s').hide();", id));
	}
	
}


