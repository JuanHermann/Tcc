package com.github.adminfaces.starter.bean;



import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

import java.util.List;

import javax.annotation.PostConstruct;

import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adminfaces.starter.model.Car;


public abstract class AbastractBean<M, R extends JpaRepository<M, Integer>> {

	private Integer id;
	private M objeto;
	private List<M> lista;
	private final Class<M> modelClass;
	@Autowired
	private R repository;
	private boolean registroSelecionado;

	AbastractBean(Class<M> modelClass) {
		this.modelClass = modelClass;
	}

	@PostConstruct
	public void inicializar() {
		listar();
	}
	
	public void init() throws InstantiationException, IllegalAccessException {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(id)) {
            objeto = (M) repository.findById(id);
        } else {
        	objeto = modelClass.newInstance();
        }
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

	
	public void alterar() {
		if(objeto == null) {
			System.out.println("Selecione um registro");
		}else {
			registroSelecionado = false;
		}
	}



	public void setRegistroSelecionado(boolean registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	protected void carregarLookups() {
		
	}
	
	public void listar() {
		lista = repository.findAll();
	}
	public void novo() throws InstantiationException, IllegalAccessException{
		objeto = modelClass.newInstance();
	}
	
	public void onRowSelect(SelectEvent event) {
		registroSelecionado = true;
	}
	
	public void remover() {
		if(objeto == null) {
			System.out.println("Selecione m registro");
			
		}else {
			repository.delete(objeto);
			addDetailMessage("Excluido com sucesso");
			objeto=null;
			listar();
		}
	}
	
	public void salvar() throws InstantiationException, IllegalAccessException {
		repository.save(objeto);		
		addDetailMessage("Salvo com sucesso");
		objeto = modelClass.newInstance();
		
	}
	
		
	
}
