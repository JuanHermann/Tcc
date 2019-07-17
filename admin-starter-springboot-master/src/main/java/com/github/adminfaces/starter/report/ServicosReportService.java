package com.github.adminfaces.starter.report;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class ServicosReportService {

	@Autowired
	private ServicosReport servicosReport;

	public JasperPrint generateRelatorioData(String titulo,String subtitulo, String caminho, LocalDate data1,LocalDate data2) throws SQLException, JRException, IOException {
		return servicosReport.generateRelatorio(titulo, caminho, data1, data2);
	}



}