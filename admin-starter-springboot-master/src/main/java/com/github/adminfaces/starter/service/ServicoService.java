/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Servico;

import java.util.List;
import java.util.function.Predicate;

public interface ServicoService extends CrudService<Servico, Integer> {

	List<String> getNomes(String query); 
	
	List<Servico> paginate(Filter<Servico> filter);
	
	List<Predicate<Servico>> configFilter(Filter<Servico> filter);
	
	long count(Filter<Servico> filter);
	
	List<Servico> listByNome(String nome);   

    void validate(Servico servico);

 
}
