package com.github.adminfaces.starter.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

@Service
public class GerarRelatorio {



    public void imprimir(HttpServletResponse response, JasperPrint jasperPrint)
            throws IOException, JRException{

        response.setContentType("application/pdf");
        OutputStream out = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
    }


    public void baixar(String nomeArquivo, HttpServletResponse response, JasperPrint jasperPrint)
            throws IOException, JRException {
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\""+nomeArquivo+"\""));
        OutputStream out = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

    }

    public byte[] gerarPdf(JasperPrint jasperPrint)
            throws JRException {

        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream outA = new ByteArrayOutputStream();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outA);
        exporter.exportReport();
        byte[] bytes = outA.toByteArray();
        return bytes;
    }





}
