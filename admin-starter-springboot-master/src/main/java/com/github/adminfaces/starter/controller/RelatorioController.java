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
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private ServicosReportService servicosReportService;

    @Autowired
    private GerarRelatorio gerarRelatorio;

    @GetMapping("/servicos")
    public void export(@RequestParam("data1") @DateTimeFormat(pattern="dd-MM-yyyy") Date data1,@RequestParam("data1") @DateTimeFormat(pattern="dd-MM-yyyy") LocalDate data2,HttpServletResponse response) throws IOException, JRException, SQLException {
        String ordem = "";
        System.out.println(data1.toString()+data2.toString());
        JasperPrint jasperPrint = servicosReportService.generateRelatorioData("Relatorio De Serviços Prestados","Entre as datas "+data1.toString() +" e "+ data2.toString(), "classpath:/reports/ServicosProcurados.jrxml",LocalDate.now(),data2);
        if (true) {
            gerarRelatorio.imprimir(response, jasperPrint);
        } else if (false) {
            gerarRelatorio.baixar("RelatorioCliente.pdf", response, jasperPrint);
        }

    }


}



