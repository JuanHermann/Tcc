/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.bean.ServicoForm;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.service.ServicoService;
import com.github.adminfaces.starter.service.impl.ServicoServiceImpl;
import org.omnifaces.util.Faces;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ServicoForm implements Serializable {

	private Integer id;
	private Servico servico;

	@Inject
	ServicoServiceImpl servicoService;

	public void init() {
		if (Faces.isAjaxRequest()) {
			return;
		}
		if (has(id)) {
			servico = servicoService.findOne(id);
		} else {
			servico = new Servico();
		}
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public void remove() throws IOException {
		if (has(servico) && has(servico.getId())) {
			servicoService.delete(servico);
			addDetailMessage("Servico " + servico.getNome() + " removed successfully");
			Faces.getFlash().setKeepMessages(true);
			Faces.redirect("servico-list.jsf");
		}
	}

	public void save() {
		String msg;
		servicoService.save(servico);
		msg = "Servico " + servico.getNome() + " created successfully";

		addDetailMessage(msg);
	}

	public void clear() {
		servico = new Servico();
		id = null;
	}

	public boolean isNew() {
		return servico == null || servico.getId() == null;
	}

}
