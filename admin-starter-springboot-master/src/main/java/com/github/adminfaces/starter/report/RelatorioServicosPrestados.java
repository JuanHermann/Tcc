package com.github.adminfaces.starter.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.report.ReportHelper.FORMATO_RELATORIO;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

@ManagedBean(name = "ServicosProcurados")
@ViewScoped
public class RelatorioServicosPrestados extends ReportItem {

	@ManagedProperty(value = "#{reportHelper}")
	private ReportHelper reportHelper;
	
	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;

	private Date dataInicial, dataFinal;

	@PostConstruct
	public void init() {
		dataInicial = new Date();
		dataFinal = new Date();
	}

	public RelatorioServicosPrestados() {
		super("Relatório de Serviços Prestados");
	}

	@Override
	public void gerarRelatorio(FORMATO_RELATORIO formato) {
		List<HorarioAgendado> dados = new ArrayList<>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("titulo", getNome());

//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		params.put("dataInicial", sdf.format(dataInicial));
//		params.put("dataFinal", sdf.format(dataFinal));

		String fileName = reportHelper.gerarRelatorio("ServicosProcurados", dados, params);
		reportHelper.abrirPoupUp(fileName);

	}

	public void gerarXLS() {
		gerarRelatorio(FORMATO_RELATORIO.XLS);
	}

	public void gerarPDF() {
		gerarRelatorio(FORMATO_RELATORIO.PDF);
	}

	public ReportHelper getReportHelper() {
		return reportHelper;
	}
	
	public void imprimeRelatorio() throws IOException, SQLException {

		List<HorarioAgendado> dados = new ArrayList<>();

		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dados);

		HashMap parameters = new HashMap();

		try {

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.responseComplete();

			ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					scontext.getRealPath("/reports/ServicosProcurados.jasper"), parameters, ds);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			JRPdfExporter exporter = new JRPdfExporter();

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

			exporter.exportReport();

			byte[] bytes = baos.toByteArray();

			if (bytes != null && bytes.length > 0) {

				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

				response.setContentType("application/pdf");

				response.setHeader("Content-disposition", "inline; filename=\"relatorioPorData.pdf\"");

				response.setContentLength(bytes.length);	
				
				ServletOutputStream outputStream = response.getOutputStream();

				outputStream.write(bytes, 0, bytes.length);

				outputStream.flush();

				outputStream.close();

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
