package com.github.adminfaces.starter.controller;

import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.service.CrudService;
import com.github.adminfaces.starter.service.impl.ServicoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ServicoController extends CrudController<Servico,Integer> {

    @Autowired
    private ServicoServiceImpl servicoService;

    @Override
    protected CrudService<Servico, Integer> getService() {
        return servicoService;
    }
}
