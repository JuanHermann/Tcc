package com.github.adminfaces.starter.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
public class ServicosReport {

	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ResourceLoader resourceLoader;

	public JasperPrint generateRelatorio(String titulo, String subtitulo, String caminho, Date data1, Date data2,
			String subReport) throws SQLException, JRException, IOException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();

		String path = resourceLoader.getResource(caminho).getURI().getPath();

		JasperReport jasperReport = JasperCompileManager.compileReport(path);
		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();

		if (!subReport.equals("")) {

			String path2 = resourceLoader.getResource(subReport).getURI().getPath();
			parameters.put("subReport", path2);
		}
		parameters.put("TITULO", titulo);
		parameters.put("SUBTITULO", subtitulo);
		parameters.put("data1", data1);
		parameters.put("data2", data2);

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);

		return print;
	}

}
