package com.github.adminfaces.starter.repository;

import com.github.adminfaces.starter.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ServicoRepository extends JpaRepository<Servico, Integer> {

	List<Servico> findByNomeLikeOrderById(String nome);
}
