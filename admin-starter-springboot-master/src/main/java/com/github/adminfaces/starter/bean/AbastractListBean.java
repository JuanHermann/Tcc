package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;


public abstract class AbastractListBean<M, R extends JpaRepository<M, Integer>> {

	private Integer id = 0;
	private M objeto;
	private List<M> lista;
	private final Class<M> modelClass;
	@Autowired
	private R repository;
    private List<M> registrosSelecionados;
    private String nome ="";

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

	public List<M> getRegistrosSelecionados() {
		return registrosSelecionados;
	}

	public void setRegistrosSelecionados(List<M> registrosSelecionados) {
		this.registrosSelecionados = registrosSelecionados;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@PostConstruct
	public void listar() {
		lista = repository.findAll();
	}

	public void novo() throws InstantiationException, IllegalAccessException {
		objeto = modelClass.newInstance();
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
	
	public void deletarSelecionados() {
		int num =0;
		for (int i = 0; i < registrosSelecionados.size(); i++) {
			repository.delete(registrosSelecionados.get(i));
			num++;
		}
		registrosSelecionados.clear();
		addDetailMessage(num + " Registros deletado com sucesso!");
		listar();
	}

	public R getRepository() {
		return repository;
	}

	public void setRepository(R repository) {
		this.repository = repository;
	}
	
	
}
