package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.service.ServicoService;
import com.github.adminfaces.starter.service.ServicoService;
import com.github.adminfaces.starter.service.impl.ServicoServiceImpl;
import com.github.adminfaces.template.exception.BusinessException;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;


@Named
@ViewScoped
public class ServicoList implements Serializable {

    @Inject
    ServicoServiceImpl servicoService;

    Integer id;

    LazyDataModel<Servico> servicos;

    Filter<Servico> filter = new Filter<>(new Servico());

    List<Servico> selectedServicos; //servicos selected in checkbox column

    List<Servico> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        servicos = new LazyDataModel<Servico>() {
            @Override
            public List<Servico> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
                com.github.adminfaces.starter.infra.model.SortOrder order = null;
                if (sortOrder != null) {
                    order = sortOrder.equals(SortOrder.ASCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.ASCENDING
                            : com.github.adminfaces.starter.infra.model.SortOrder.UNSORTED;
                }
                filter.setFirst(first).setPageSize(pageSize)
                        .setSortField(sortField).setSortOrder(order)
                        .setParams(filters);
                List<Servico> list = servicoService.paginate(filter);
                setRowCount((int) servicoService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Servico getRowData(String key) {
                return servicoService.findOne(new Long(key));
            }
        };
    }


    public void clear() {
        filter = new Filter<Servico>(new Servico());
    }
    
    public List<String> completeModel(String query) {
        List<String> result = servicoService.getNomes(query);
        return result;
    }



    public void findServicoById(Long id) {
        if (id == null) {
            throw new BusinessException("Provide Servico ID to load");
        }
        selectedServicos.add(servicoService.findOne(id));
    }

    public void delete() {
        int numServicos = 0;
        for (Servico selectedServico : selectedServicos) {
            numServicos++;
            servicoService.delete(selectedServico);
        }
        selectedServicos.clear();
        addDetailMessage(numServicos + " servicos deleted successfully!");
    }

    public List<Servico> getSelectedServico() {
        return selectedServicos;
    }

    public List<Servico> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Servico> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void setSelectedServico(List<Servico> selectedServico) {
        this.selectedServicos = selectedServico;
    }

    public LazyDataModel<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(LazyDataModel<Servico> servicos) {
        this.servicos = servicos;
    }

    public Filter<Servico> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Servico> filter) {
        this.filter = filter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
