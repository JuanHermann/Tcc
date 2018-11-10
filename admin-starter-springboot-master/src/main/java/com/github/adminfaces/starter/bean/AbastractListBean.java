package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.List;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;


public abstract class AbastractListBean<M, R extends JpaRepository<M, Integer>> {

	private Integer id = 0;
	private M objeto;
	private List<M> lista;
	private final Class<M> modelClass;
	@Autowired
	private R repository;
	private boolean registroSelecionado;

	AbastractListBean(Class<M> modelClass) {
		this.modelClass = modelClass;
	}

	



	public M getObjeto() {
		return objeto;
	}

	public void setObjeto(M objeto) {
		this.objeto = objeto;
	}

	public List<M> getLista() {
		return lista;
	}

	public void setLista(List<M> lista) {
		this.lista = lista;
	}

	public boolean isRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(boolean registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	protected void carregarLookups() {

	}

	public void listar() {
		lista = repository.findAll();
	}
	
	public void buscar() {
		lista = repository.findAll();
	}

	public void novo() throws InstantiationException, IllegalAccessException {
		objeto = modelClass.newInstance();
	}

	public void onRowSelect(SelectEvent event) {
		registroSelecionado = true;
	}

	public void remover() throws InstantiationException, IllegalAccessException {
		if (objeto == null) {
			addDetailMessage("Nenhum registro para Excluir");

		} else {
			repository.delete(objeto);
			objeto = modelClass.newInstance();
			addDetailMessage("Excluido com sucesso");
		}
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
