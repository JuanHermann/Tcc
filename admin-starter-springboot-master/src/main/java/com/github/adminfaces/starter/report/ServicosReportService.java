package com.github.adminfaces.starter.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class ServicosReportService {

	@Autowired
	private ServicosReport servicosReport;

	public JasperPrint generateRelatorioData(String titulo,String subtitulo, String caminho, Date data1,Date data2) throws SQLException, JRException, IOException {
		return servicosReport.generateRelatorio(titulo,subtitulo, caminho, data1, data2);
	}



}