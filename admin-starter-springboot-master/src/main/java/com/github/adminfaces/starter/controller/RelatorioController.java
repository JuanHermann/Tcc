package com.github.adminfaces.starter.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.github.adminfaces.starter.report.ServicosReportService;
import com.github.adminfaces.starter.util.GerarRelatorio;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private ServicosReportService servicosReportService;

    @Autowired
    private GerarRelatorio gerarRelatorio;

    @GetMapping("/servicos")
    public void export(@RequestParam("data1") @DateTimeFormat(pattern="yyyy-MM-dd") Date data1,@RequestParam("data2") @DateTimeFormat(pattern="yyyy-MM-dd") Date data2,HttpServletResponse response) throws IOException, JRException, SQLException {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JasperPrint jasperPrint = servicosReportService.generateRelatorioData("Relatório De Serviços Prestados","Entre as datas "+sdf.format(data1) +" e "+ sdf.format(data2), "classpath:/reports/ServicosProcurados.jrxml",data1,data2);
        if (true) {
            gerarRelatorio.imprimir(response, jasperPrint);
        } else if (false) {
            gerarRelatorio.baixar("RelatorioCliente.pdf", response, jasperPrint);
        }

    }


}



