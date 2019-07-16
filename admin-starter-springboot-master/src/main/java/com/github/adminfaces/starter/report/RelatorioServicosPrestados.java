package com.github.adminfaces.starter.report;

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

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.report.ReportHelper.FORMATO_RELATORIO;

@ManagedBean(name = "r001")
@ViewScoped
public class RelatorioServicosPrestados extends ReportItem {

	@ManagedProperty(value = "#{reportHelper}")
	private ReportHelper reportHelper;

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
		List<HorarioAgendado> funcionarios = new ArrayList<>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("titulo", getNome());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		params.put("dataInicial", sdf.format(dataInicial));
		params.put("dataFinal", sdf.format(dataFinal));

		String fileName = reportHelper.gerarRelatorio("r001", funcionarios, params);
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
}
