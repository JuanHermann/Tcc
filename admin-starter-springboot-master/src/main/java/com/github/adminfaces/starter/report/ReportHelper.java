package com.github.adminfaces.starter.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "reportHelper")
public class ReportHelper {

	public enum FORMATO_RELATORIO {
		XLS, PDF;
	}

	private String gerarIdReport() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
		String id = simpleDateFormat.format(new Date());

		return id;
	}

	/**
	 * Retorna caminho onde os relatórios (.jasper e .jrxml e tmps) ficam
	 * armazenados
	 */
	private String reportSourcePath() {

		return FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB INF/report/") + "/";
	}

	/**
	 * Retorna o caminho onde os relatórios finais ficam no servidor (ex: .PDF)
	 */
	private String reportFile() {
		return FacesContext.getCurrentInstance().getExternalContext().getInitParameter("reportDirectory");
	}

	/**
	 * Abrir Janela com Arquivo (PDF, XLS, TXT e etc)
	 */
	public void abrirPoupUp(String fileName) {
		abrirPoupUp(fileName, null);
	}

	public void abrirPoupUp(String fileName, String nomeJanela) {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		String contextPath = req.getContextPath().replace("/", "");
		if (nomeJanela == null) {
			nomeJanela = "Relatório";
		}

		RequestContext.getCurrentInstance().execute("window.open('/" + contextPath + "/report/" + fileName + ",'"
				+ nomeJanela + "','width=800px,height=800px')");
	}

	/**
	 * Gera o relatório e retorna o nome do relatório gerado
	 */
	private String gerarRelatorio(String relatorio, List beans, Map<String, Object> params,
			FORMATO_RELATORIO formatoExportacao) {
		try {
			if (params == null) {
				params = new HashMap<String, Object>();
			}

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(beans);

			String relatorioFormated = relatorio.endsWith(".jasper") ? relatorio
					: (new StringBuilder()).append(relatorio).append(".jasper").toString();
			net.sf.jasperreports.engine.JasperPrint jasperPrint = null;

			if (beans != null && beans.size() > 0) {
				jasperPrint = JasperFillManager.fillReport(reportSourcePath() + relatorioFormated, params,
						beanCollectionDataSource);
			} else {
				jasperPrint = JasperFillManager.fillReport(reportSourcePath() + relatorioFormated, params,
						new JREmptyDataSource());
			}

			StringBuilder nomeRelatorio = new StringBuilder();
			nomeRelatorio.append(reportFile() + relatorio + "_" + gerarIdReport());

			JRExporter exporter = null;

			if (formatoExportacao.equals(FORMATO_RELATORIO.PDF)) {
				exporter = new JRPdfExporter();
				nomeRelatorio.append(".pdf");
			} else if (formatoExportacao.equals(FORMATO_RELATORIO.XLS)) {
				exporter = new JRXlsExporter();
				nomeRelatorio.append(".xls");
			}

			File fTarget = new File(nomeRelatorio.toString());

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(fTarget.getAbsolutePath()));
			exporter.exportReport();

			return fTarget.getName();
		} catch (JRException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Geração de Relatório em XLS
	 */
	public String gerarRelatorioXLS(String relatorio, List beans, Map<String, Object> params) {

		return gerarRelatorio(relatorio, beans, params, FORMATO_RELATORIO.XLS);

	}

	/**
	 * Padrão para Arquivos PDF
	 */
	public String gerarRelatorio(String relatorio, List beans, Map<String, Object> params) {

		return gerarRelatorio(relatorio, beans, params, FORMATO_RELATORIO.PDF);

	}

}
