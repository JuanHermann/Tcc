package com.github.adminfaces.starter.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.adminfaces.starter.report.ServicosReportService;
import com.github.adminfaces.starter.util.GerarRelatorio;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private ServicosReportService servicosReportService;

    @Autowired
    private GerarRelatorio gerarRelatorio;

    @GetMapping("/servicos")
    public void exportServicos(@RequestParam("data1") @DateTimeFormat(pattern="yyyy-MM-dd") Date data1,@RequestParam("data2") @DateTimeFormat(pattern="yyyy-MM-dd") Date data2,HttpServletResponse response) throws IOException, JRException, SQLException {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JasperPrint jasperPrint = servicosReportService.generateRelatorioData("Número de Serviços Prestados","Entre as datas "+sdf.format(data1) +" e "+ sdf.format(data2), "classpath:/reports/ServicosProcurados.jrxml",data1,data2,"");
//        if (true) {
            gerarRelatorio.imprimir(response, jasperPrint);
//        } else if (false) {
//            gerarRelatorio.baixar("ServicosProcurados.pdf", response, jasperPrint);
//        }

    }
    
    @GetMapping("/horarios")
    public void exportHorarios(@RequestParam("data1") @DateTimeFormat(pattern="yyyy-MM-dd") Date data1,@RequestParam("data2") @DateTimeFormat(pattern="yyyy-MM-dd") Date data2,HttpServletResponse response) throws IOException, JRException, SQLException {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JasperPrint jasperPrint = servicosReportService.generateRelatorioData("Horários Preferidos","Entre as datas "+sdf.format(data1) +" e "+ sdf.format(data2), "classpath:/reports/HorariosProcurados.jrxml",data1,data2,"");

        gerarRelatorio.imprimir(response, jasperPrint);


    }
    
    @GetMapping("/entradas")
    public void exportEntradas(@RequestParam("data1") @DateTimeFormat(pattern="yyyy-MM-dd") Date data1,@RequestParam("data2") @DateTimeFormat(pattern="yyyy-MM-dd") Date data2,HttpServletResponse response) throws IOException, JRException, SQLException {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JasperPrint jasperPrint = servicosReportService.generateRelatorioData("Entrada Mensal","Entre as datas "+sdf.format(data1) +" e "+ sdf.format(data2), "classpath:/reports/EntradaMensal.jrxml",data1,data2,"");
        
        gerarRelatorio.imprimir(response, jasperPrint);
       

    }
    
    @GetMapping("/funcionarios")
    public void exportfuncionarios(@RequestParam("data1") @DateTimeFormat(pattern="yyyy-MM-dd") Date data1,@RequestParam("data2") @DateTimeFormat(pattern="yyyy-MM-dd") Date data2,HttpServletResponse response) throws IOException, JRException, SQLException {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JasperPrint jasperPrint = servicosReportService.generateRelatorioData("Serviços Realizados","Entre as datas "+sdf.format(data1) +" e "+ sdf.format(data2), "classpath:/reports/FuncionariosServicos.jrxml",data1,data2,"classpath:/reports/ServicosFuncionarios.jasper");
        
        gerarRelatorio.imprimir(response, jasperPrint);
       

    }


}



