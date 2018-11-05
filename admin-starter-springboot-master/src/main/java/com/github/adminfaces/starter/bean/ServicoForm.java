/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.bean.ServicoForm;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

@Component
@Scope("view")
public class ServicoForm {

	private Integer id = 0;
	private Servico objeto;
	@Autowired
	private ServicoRepository repository;

	@PostConstruct
	public void inicializar() throws InstantiationException, IllegalAccessException {
		if (Faces.isAjaxRequest()) {
			return;
		}
		if (has(id)) {
			objeto = repository.findById(id).orElse(null);
		} else {
			objeto = new Servico();
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Servico getObjeto() {
		return objeto;
	}

	public void setObjeto(Servico objeto) {
		this.objeto = objeto;
	}

	public ServicoRepository getRepository() {
		return repository;
	}

	public void setRepository(ServicoRepository repository) {
		this.repository = repository;
	}

	public void novo() throws InstantiationException, IllegalAccessException {
		objeto = new Servico();
	}

	public void remover() throws InstantiationException, IllegalAccessException {
		if (objeto == null) {
			addDetailMessage("Nenhum registro para Excluir");

		} else {
			repository.delete(objeto);
			objeto = new Servico();
			addDetailMessage("Excluido com sucesso");
		}
	}

	public void salvar() throws InstantiationException, IllegalAccessException {
		if (objeto == null) {
			addDetailMessage("Objeto nulo");
		} else {
			repository.save(objeto);
			addDetailMessage("Salvo com sucesso");
			objeto = new Servico();
		}

	}

}
