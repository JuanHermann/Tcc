package com.github.adminfaces.starter.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.adminfaces.starter.report.SeguroReportService;
import com.github.adminfaces.starter.util.GerarRelatorio;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private SeguroReportService seguroReportService;

    @Autowired
    private GerarRelatorio gerarRelatorio;

    @GetMapping("/servicos")
    public void export(HttpServletResponse response) throws IOException, JRException, SQLException {
        String ordem = "";
        JasperPrint jasperPrint = seguroReportService.generatePromissoria(1L, "Relatorio de Seguros por Cliente", "cliente", "classpath:/reports/ServicosProcurados.jrxml", ordem, ordem);
        if (true) {
            gerarRelatorio.imprimir(response, jasperPrint);
        } else if (false) {
            gerarRelatorio.baixar("RelatorioCliente.pdf", response, jasperPrint);
        }

    }


}



