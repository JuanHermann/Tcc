package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class ServicoList extends AbastractListBean<Servico, ServicoRepository> {

	@Autowired
	private ServicoRepository servicoRepository;

	public ServicoList() {
		super(Servico.class);
	}

	public void buscar() {
		if (getNome() != "") {
			setLista(servicoRepository.findByNomeLikeOrderById("%"+getNome()+"%"));
		} else {
			setLista(servicoRepository.findAll());
		}

	}

	public void onRowSelect(SelectEvent event){
		   Servico produtoTemp = ((Servico) event.getObject()); 
		   produtoTemp.setEnableItem(true);
		}

		public void unRowSelect(UnselectEvent event){
		   Servico produtoTemp = ((Servico) event.getObject()); 
		   produtoTemp.setEnableItem(false);
		}

		public void onRowSelectAll(ToggleSelectEvent event){
		   DataTable listTemp = (DataTable) event.getSource();
		   List<Servico> list = (List<Servico>) listTemp.getValue();
		   if(event.isSelected()){
		      for(Servico p: list){
		        if(p.getEstoqueMaximo() > 0)
		       p.setEnableItem(true);
		      }
		      return;
		   }
		   for(Servico p: list){
		      if(p.getEstoqueMaximo() > 0)
		    p.setEnableItem(false);
		      }
		}

}
