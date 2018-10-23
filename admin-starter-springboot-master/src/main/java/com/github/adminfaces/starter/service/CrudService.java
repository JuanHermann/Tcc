package com.github.adminfaces.starter.service;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Car;

public interface CrudService <T, ID extends Serializable> {

	
	List<T> findAll();
	
	List<T> findAll(Sort sort);
	
	Page<T> findAll(Pageable pageable);

	T save(T entity);
	
	T saveAndFlush(T entity);

	Iterable<T> save(Iterable<T> iterable);
	
	void flush();
	
	T findOne(ID id);
	
	boolean exists(ID id);
	
	long count();
	
	void delete(ID id);
	
	void delete(T entity);
	
	void delete(Iterable<T> iterable);
	
	void deleteAll();
		
}






