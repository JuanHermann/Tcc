package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbastractFormBean<M, R extends JpaRepository<M, Integer>> {

	private Integer id = 0;
	private M objeto;
	private final Class<M> modelClass;
	@Autowired
	private R repository;

	AbastractFormBean(Class<M> modelClass) {
		this.modelClass = modelClass;
	}

	
    @PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		if (id==0) {
			novo();
			//System.out.println("novo");
		} else {

			objeto = (M) repository.findById(id).orElse(null);
			//System.out.println("id");
		}
	}


	public void novo() throws InstantiationException, IllegalAccessException {
		objeto = modelClass.newInstance();
	}

	public void remover() throws InstantiationException, IllegalAccessException {
		if (objeto == null) {
			addDetailMessage("Nenhum registro para Excluir");

		} else {
			repository.delete(objeto);
			novo();
			addDetailMessage("Excluido com sucesso");		
			
		}
	}

	public void salvar() throws InstantiationException, IllegalAccessException {
		System.out.println("salvar");
		if (objeto == null) {
			addDetailMessage("Objeto nulo");
		} else {
			repository.save(objeto);
			addDetailMessage("Salvo com sucesso");
			objeto = modelClass.newInstance();
		}
		

	}

}
