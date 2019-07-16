package com.github.adminfaces.starter.report;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class SeguroReportService {

	@Autowired
	private SeguroReport seguroReport;

	public JasperPrint generatePromissoria(Long id, String titulo, String descricao, String caminho, String ordem, String sub) throws SQLException, JRException, IOException {
		return seguroReport.generatePromissoria(id, titulo, descricao, caminho, ordem, sub);
	}


	public JasperPrint generateRelatorioData(Long id, String titulo, Date data1, Date data2, String caminho, String sub) throws SQLException, JRException, IOException {
		return seguroReport.generateRelatorioData(id, titulo, data1, data2, caminho, sub);
	}

}