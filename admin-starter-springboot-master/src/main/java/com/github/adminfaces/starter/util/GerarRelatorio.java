package com.github.adminfaces.starter.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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



    public Date data2(String data1, String data2) throws ParseException {
//        Date dt1 = new SimpleDateFormat("dd/MM/yyyy").parse(data1);
        Date dt2;
        if (data2.isEmpty()){
            String novaData = data1.substring(6);
            int i = Integer.parseInt(novaData)+1;
            data2 = data1.substring(0, 6)+i;
            dt2 = new SimpleDateFormat("dd/MM/yyyy").parse(data2);
        }else{
            dt2 = new SimpleDateFormat("dd/MM/yyyy").parse(data2);
        }
        return dt2;
    }

}
