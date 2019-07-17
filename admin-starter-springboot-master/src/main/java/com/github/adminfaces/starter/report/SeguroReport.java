package com.github.adminfaces.starter.report;

import java.awt.Image;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Transactional
@Repository
public class SeguroReport {

	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ResourceLoader resourceLoader;

	public JasperPrint generatePromissoria(Long id, String titulo, String descricao, String caminho, String ordem, String sub) throws SQLException, JRException, IOException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();

		String path = resourceLoader.getResource(caminho).getURI().getPath();

		JasperReport jasperReport = JasperCompileManager.compileReport(path);
		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("TITULO", titulo);
//		parameters.put("ID", id);
//		parameters.put("DESCRICAO", descricao);
//		parameters.put("ORDEM", ordem);
//		parameters.put("SUBTITULO", sub);

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);

		return print;
	}

	public JasperPrint generateRelatorioData(Long id, String titulo, Date data1, Date data2, String caminho, String sub) throws SQLException, JRException, IOException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		String path = resourceLoader.getResource(caminho).getURI().getPath();
		JasperReport jasperReport = JasperCompileManager.compileReport(path);
		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("TITULO", titulo);
		parameters.put("ID", id);
		parameters.put("data1", data1);
		parameters.put("data2", data2);
		parameters.put("SUBTITULO", sub);

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);

		return print;
	}
}
