package com.github.adminfaces.starter.service.impl;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.service.ServicoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Service
public class ServicoServiceImpl extends CrudServiceImpl<Servico, Integer> implements ServicoService {

	
	List<Servico> allServico;

	@Autowired
	private ServicoRepository servicoRepository;

	@Override
	protected JpaRepository<Servico, Integer> getRepository() {
		return servicoRepository;
	}

	@Override
	public List<String> getNomes(String query) {
		return allServico.stream().filter(c -> c.getNome().toLowerCase().contains(query.toLowerCase()))
				.map(Servico::getNome).collect(Collectors.toList());
	}

	@Override
	public List<Servico> paginate(Filter<Servico> filter) {
		List<Servico> pagedServicos = new ArrayList<>();
		if (has(filter.getSortOrder()) && !SortOrder.UNSORTED.equals(filter.getSortOrder())) {
			pagedServicos = allServico.stream().sorted((c1, c2) -> {
				if (filter.getSortOrder().isAscending()) {
					return c1.getId().compareTo(c2.getId());
				} else {
					return c2.getId().compareTo(c1.getId());
				}
			}).collect(Collectors.toList());
		}

		int page = filter.getFirst() + filter.getPageSize();
		if (filter.getParams().isEmpty()) {
			pagedServicos = pagedServicos.subList(filter.getFirst(),
					page > allServico.size() ? allServico.size() : page);
			return pagedServicos;
		}

		List<Predicate<Servico>> predicates = configFilter(filter);

		List<Servico> pagedList = allServico.stream()
				.filter(predicates.stream().reduce(Predicate::or).orElse(t -> true)).collect(Collectors.toList());

		if (page < pagedList.size()) {
			pagedList = pagedList.subList(filter.getFirst(), page);
		}

		if (has(filter.getSortField())) {
			pagedList = pagedList.stream().sorted((c1, c2) -> {
				boolean asc = SortOrder.ASCENDING.equals(filter.getSortOrder());
				if (asc) {
					return c1.getId().compareTo(c2.getId());
				} else {
					return c2.getId().compareTo(c1.getId());
				}
			}).collect(Collectors.toList());
		}
		return pagedList;
	}

	@Override
	public List<Predicate<Servico>> configFilter(Filter<Servico> filter) {
		List<Predicate<Servico>> predicates = new ArrayList<>();
		if (filter.hasParam("id")) {
			Predicate<Servico> idPredicate = (Servico c) -> c.getId().equals(filter.getParam("id"));
			predicates.add(idPredicate);
		}

		if (filter.hasParam("minPrice") && filter.hasParam("maxPrice")) {
			Predicate<Servico> minMaxPricePredicate = (
					Servico c) -> c.getValor() >= Double.valueOf((String) filter.getParam("minPrice"))
							&& c.getValor() <= Double.valueOf((String) filter.getParam("maxPrice"));
			predicates.add(minMaxPricePredicate);
		} else if (filter.hasParam("minPrice")) {
			Predicate<Servico> minPricePredicate = (
					Servico c) -> c.getValor() >= Double.valueOf((String) filter.getParam("minPrice"));
			predicates.add(minPricePredicate);
		} else if (filter.hasParam("maxPrice")) {
			Predicate<Servico> maxPricePredicate = (
					Servico c) -> c.getValor() <= Double.valueOf((String) filter.getParam("maxPrice"));
			predicates.add(maxPricePredicate);
		}

		if (has(filter.getEntity())) {
			Servico filterEntity = filter.getEntity();
			if (has(filterEntity.getNome())) {
				Predicate<Servico> modelPredicate = (Servico c) -> c.getNome().toLowerCase()
						.contains(filterEntity.getNome().toLowerCase());
				predicates.add(modelPredicate);
			}

			if (has(filterEntity.getDescricao())) {
				Predicate<Servico> pricePredicate = (Servico c) -> c.getDescricao().equals(filterEntity.getDescricao());
				predicates.add(pricePredicate);
			}

		}
		return predicates;
	}

	public long count(Filter<Servico> filter) {
		return allServico.stream().filter(configFilter(filter).stream().reduce(Predicate::or).orElse(t -> true))
				.count();
	}
	@Override
	public List<Servico> listByNome(String nome) {
		return allServico.stream().filter(c -> c.getNome().equalsIgnoreCase(nome)).collect(Collectors.toList());

	}

	@Override
	public void validate(Servico servico) {
		System.out.println("Valido");
	}

}
