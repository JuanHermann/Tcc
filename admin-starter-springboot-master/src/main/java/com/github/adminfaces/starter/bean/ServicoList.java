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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;


@Component
@Scope("view")
public class ServicoList implements Serializable {

   
}
