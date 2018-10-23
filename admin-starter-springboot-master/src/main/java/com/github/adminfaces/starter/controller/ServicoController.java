package com.github.adminfaces.starter.controller;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.service.CrudService;
import com.github.adminfaces.starter.service.impl.ServicoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ServicoController extends CrudController<Servico,Long> {

    @Autowired
    private ServicoServiceImpl servicoService;

    @Override
    protected CrudService<Servico, Long> getService() {
        return servicoService;
    }
}
